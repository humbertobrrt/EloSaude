package com.elo.saude.dev.projeto.controller;

import com.elo.saude.dev.projeto.dto.atendimento.SolicitacaoCreateRequest;
import com.elo.saude.dev.projeto.dto.atendimento.SolicitacaoResponse;
import com.elo.saude.dev.projeto.dto.atendimento.SolicitacaoStatusRequest;
import com.elo.saude.dev.projeto.enums.StatusSolicitacao;
import com.elo.saude.dev.projeto.service.SolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PACIENTE')")
    public SolicitacaoResponse criar(Authentication auth,
                                     @Valid @RequestBody SolicitacaoCreateRequest req) {
        return service.criar(auth.getName(), req);
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('PACIENTE')")
    public List<SolicitacaoResponse> minhas(Authentication auth) {
        return service.listarMinhas(auth.getName());
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('PACIENTE')")
    public SolicitacaoResponse cancelar(Authentication auth, @PathVariable Long id) {
        return service.cancelar(id, auth.getName());
    }

    @GetMapping("/unidade")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public List<SolicitacaoResponse> daUnidade(
            Authentication auth,
            @RequestParam(required = false) StatusSolicitacao status) {
        return service.listarDaUnidade(auth.getName(), status);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public SolicitacaoResponse atualizarStatus(Authentication auth, @PathVariable Long id,
                                               @Valid @RequestBody SolicitacaoStatusRequest req) {
        return service.atualizarStatus(id, auth.getName(), req);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE','PROFISSIONAL')")
    public SolicitacaoResponse buscar(Authentication auth, @PathVariable Long id) {
        return service.buscar(id, auth.getName());
    }
}
