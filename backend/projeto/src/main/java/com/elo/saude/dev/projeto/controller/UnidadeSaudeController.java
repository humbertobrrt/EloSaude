
package com.elo.saude.dev.projeto.controller;

import com.elo.saude.dev.projeto.dto.unidadesaude.UnidadeSaudeCreateRequest;
import com.elo.saude.dev.projeto.dto.unidadesaude.UnidadeSaudeResponse;
import com.elo.saude.dev.projeto.dto.unidadesaude.UnidadeSaudeUpdateRequest;
import com.elo.saude.dev.projeto.service.UnidadeSaudeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades-saude")
public class UnidadeSaudeController {

    private final UnidadeSaudeService service;

    public UnidadeSaudeController(UnidadeSaudeService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UnidadeSaudeResponse criar(
            @Valid @RequestBody UnidadeSaudeCreateRequest req) {
        return UnidadeSaudeResponse.from(service.criar(req));
    }

    @GetMapping
    public List<UnidadeSaudeResponse> listar() {
        return service.listar()
                .stream()
                .map(UnidadeSaudeResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public UnidadeSaudeResponse buscar(@PathVariable Long id) {
        return UnidadeSaudeResponse.from(service.buscarPorId(id));
    }

    @GetMapping("/cnes/{cnes}")
    public UnidadeSaudeResponse buscarPorCnes(
            @PathVariable String cnes) {
        return UnidadeSaudeResponse.from(service.buscarPorCnes(cnes));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UnidadeSaudeResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UnidadeSaudeUpdateRequest req) {
        return UnidadeSaudeResponse.from(
                service.atualizar(id, req)
        );
    }

    @PatchMapping("/{id}/ativa")
    @PreAuthorize("hasRole('ADMIN')")
    public UnidadeSaudeResponse alterarAtiva(
            @PathVariable Long id,
            @RequestParam boolean valor) {
        return UnidadeSaudeResponse.from(
                service.alterarAtiva(id, valor)
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
