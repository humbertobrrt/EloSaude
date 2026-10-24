
package com.elo.saude.dev.projeto.controller;

import com.elo.saude.dev.projeto.entity.ContraReferencia;
import com.elo.saude.dev.projeto.service.ContraReferenciaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contra-referencias")
public class ContraReferenciaController {

    private final ContraReferenciaService service;

    public ContraReferenciaController(
            ContraReferenciaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ContraReferencia>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContraReferencia> buscarPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/encaminhamento/{encaminhamentoId}")
    public ResponseEntity<ContraReferencia> buscarPorEncaminhamento(
            @PathVariable Long encaminhamentoId) {
        return ResponseEntity.ok(
                service.buscarPorEncaminhamento(encaminhamentoId));
    }

    @PostMapping
    public ResponseEntity<ContraReferencia> cadastrar(
            @RequestBody ContraReferencia contraReferencia) {

        ContraReferencia criada = service.cadastrar(contraReferencia);

        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContraReferencia> atualizar(
            @PathVariable Long id,
            @RequestBody ContraReferencia contraReferencia) {

        return ResponseEntity.ok(
                service.atualizar(id, contraReferencia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

