
package com.elo.saude.dev.projeto.controller;

import com.elo.saude.dev.projeto.entity.Encaminhamento;
import com.elo.saude.dev.projeto.service.EncaminhamentoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encaminhamentos")
public class EncaminhamentoController {

    private final EncaminhamentoService service;

    public EncaminhamentoController(
            EncaminhamentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Encaminhamento>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Encaminhamento> buscarPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Encaminhamento>> buscarPorPaciente(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(
                service.buscarPorPaciente(pacienteId));
    }

    @GetMapping("/unidade-origem/{unidadeId}")
    public ResponseEntity<List<Encaminhamento>> buscarPorUnidadeOrigem(
            @PathVariable Long unidadeId) {
        return ResponseEntity.ok(
                service.buscarPorUnidadeOrigem(unidadeId));
    }

    @GetMapping("/unidade-destino/{unidadeId}")
    public ResponseEntity<List<Encaminhamento>> buscarPorUnidadeDestino(
            @PathVariable Long unidadeId) {
        return ResponseEntity.ok(
                service.buscarPorUnidadeDestino(unidadeId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Encaminhamento>> buscarPorStatus(
            @PathVariable Encaminhamento.StatusEncaminhamento status) {
        return ResponseEntity.ok(service.buscarPorStatus(status));
    }

    @GetMapping("/prioridade/{prioridade}")
    public ResponseEntity<List<Encaminhamento>> buscarPorPrioridade(
            @PathVariable Encaminhamento.Prioridade prioridade) {
        return ResponseEntity.ok(
                service.buscarPorPrioridade(prioridade));
    }

    @PostMapping
    public ResponseEntity<Encaminhamento> cadastrar(
            @RequestBody Encaminhamento encaminhamento) {

        Encaminhamento criado = service.cadastrar(encaminhamento);

        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Encaminhamento> atualizarStatus(
            @PathVariable Long id,
            @RequestParam
            Encaminhamento.StatusEncaminhamento status) {

        return ResponseEntity.ok(
                service.atualizarStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

