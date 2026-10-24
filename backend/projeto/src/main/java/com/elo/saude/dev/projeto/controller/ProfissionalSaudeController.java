
package com.elo.saude.dev.projeto.controller;

import com.elo.saude.dev.projeto.entity.ProfissionalSaude;
import com.elo.saude.dev.projeto.repository.ProfissionalSaudeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profissionais")
public class ProfissionalSaudeController {

    private final ProfissionalSaudeRepository profissionalRepository;

    public ProfissionalSaudeController(
            ProfissionalSaudeRepository profissionalRepository) {
        this.profissionalRepository = profissionalRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProfissionalSaude>> listarTodos() {
        return ResponseEntity.ok(profissionalRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalSaude> buscarPorId(
            @PathVariable Long id) {

        return profissionalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ProfissionalSaude> buscarPorUsuario(
            @PathVariable Long usuarioId) {

        return profissionalRepository.findByUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/unidade/{unidadeId}")
    public ResponseEntity<List<ProfissionalSaude>> listarPorUnidade(
            @PathVariable Long unidadeId) {

        return ResponseEntity.ok(
                profissionalRepository.findByUnidadeId(unidadeId));
    }

    @PostMapping
    public ResponseEntity<ProfissionalSaude> cadastrar(
            @RequestBody ProfissionalSaude profissional) {

        ProfissionalSaude salvo =
                profissionalRepository.save(profissional);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalSaude> atualizar(
            @PathVariable Long id,
            @RequestBody ProfissionalSaude profissional) {

        if (!profissionalRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        profissional.setId(id);

        return ResponseEntity.ok(
                profissionalRepository.save(profissional));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (!profissionalRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        profissionalRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

