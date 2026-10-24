package com.elo.saude.dev.projeto.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - @Valid falhou em algum DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException ex,
                                                  HttpServletRequest req) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos", campos, req);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> corpoIlegivel(HttpMessageNotReadableException ex,
                                                      HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Corpo da requisição ausente, mal formado ou com valor inválido", null, req);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tipoInvalido(MethodArgumentTypeMismatchException ex,
                                                     HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Valor inválido para o parâmetro '" + ex.getName() + "'", null, req);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> statusExplicito(ResponseStatusException ex,
                                                        HttpServletRequest req) {
        String mensagem = ex.getReason() != null ? ex.getReason() : "Erro na requisição";
        return resposta(ex.getStatusCode(), mensagem, null, req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> integridade(DataIntegrityViolationException ex,
                                                    HttpServletRequest req) {
        log.warn("Violação de integridade em {}: {}", req.getRequestURI(),
                ex.getMostSpecificCause().getMessage());
        return resposta(HttpStatus.CONFLICT,
                "A operação viola uma regra de integridade dos dados "
                        + "(registro duplicado ou referência inexistente)", null, req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> acessoNegado(AccessDeniedException ex,
                                                     HttpServletRequest req) {
        return resposta(HttpStatus.FORBIDDEN, "Acesso negado para o seu perfil", null, req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(NoResourceFoundException ex,
                                                      HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", null, req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> metodoNaoSuportado(HttpRequestMethodNotSupportedException ex,
                                                           HttpServletRequest req) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED,
                "Método " + ex.getMethod() + " não suportado para esta rota", null, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> inesperado(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado em {} {}", req.getMethod(), req.getRequestURI(), ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno. Tente novamente mais tarde.", null, req);
    }

    private ResponseEntity<ErroResponse> resposta(HttpStatusCode status, String mensagem,
                                                  Map<String, String> campos,
                                                  HttpServletRequest req) {
        HttpStatus http = HttpStatus.resolve(status.value());
        String erro = http != null ? http.getReasonPhrase() : "Erro";
        ErroResponse corpo = new ErroResponse(LocalDateTime.now(), status.value(), erro,
                mensagem, req.getRequestURI(), campos);
        return ResponseEntity.status(status).body(corpo);
    }
}

