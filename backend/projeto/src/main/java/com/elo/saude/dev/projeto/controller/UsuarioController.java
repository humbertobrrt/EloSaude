package com.elo.saude.dev.projeto.controller;


import com.elo.saude.dev.projeto.*;
import com.elo.saude.dev.projeto.dto.AtualizarNomeRequest;
import com.elo.saude.dev.projeto.dto.RegistroRequest;
import com.elo.saude.dev.projeto.dto.UsuarioCreateRequest;
import com.elo.saude.dev.projeto.dto.UsuarioResponse;
import com.elo.saude.dev.projeto.enuns.Perfil;
import com.elo.saude.dev.projeto.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // Público: cadastro de paciente
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest req) {
        return UsuarioResponse.from(
                service.criar(req.nome(), req.email(), req.senha(), Perfil.PACIENTE));
    }

    // Qualquer usuário logado
    @GetMapping("/me")
    public UsuarioResponse me(Authentication auth) {
        return UsuarioResponse.from(service.buscarPorEmail(auth.getName()));
    }

    @PatchMapping("/me")
    public UsuarioResponse atualizarMeuNome(Authentication auth,
                                            @Valid @RequestBody AtualizarNomeRequest req) {
        return UsuarioResponse.from(service.atualizarNome(auth.getName(), req.nome()));
    }

    // Somente ADMIN
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse criar(@Valid @RequestBody UsuarioCreateRequest req) {
        return UsuarioResponse.from(
                service.criar(req.nome(), req.email(), req.senha(), req.perfil()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponse> listar() {
        return service.listar().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return UsuarioResponse.from(service.buscarPorId(id));
    }

    @PatchMapping("/{id}/ativo")
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse alterarAtivo(@PathVariable Long id, @RequestParam boolean valor) {
        return UsuarioResponse.from(service.alterarAtivo(id, valor));
    }
}
