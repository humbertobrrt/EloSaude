package com.elo.saude.dev.projeto.controller;

import com.elo.saude.dev.projeto.dto.atendimento.AtendimentoCreateRequest;
import com.elo.saude.dev.projeto.dto.atendimento.AtendimentoResponse;
import com.elo.saude.dev.projeto.service.AtendimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
public class AtendimentoController {

    private final AtendimentoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public AtendimentoResponse registrar(Authentication auth,
                                         @Valid @RequestBody AtendimentoCreateRequest req) {
        return service.registrar(auth.getName(), req);
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('PACIENTE')")
    public List<AtendimentoResponse> meus(Authentication auth) {
        return service.meuHistorico(auth.getName());
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public List<AtendimentoResponse> doPaciente(Authentication auth,
                                                @PathVariable Long pacienteId) {
        return service.historicoDoPaciente(auth.getName(), pacienteId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE','PROFISSIONAL')")
    public AtendimentoResponse buscar(Authentication auth, @PathVariable Long id) {
        return service.buscar(id, auth.getName());
    }
}
