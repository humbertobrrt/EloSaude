
package com.elo.saude.dev.projeto.controller;

import com.elo.saude.dev.projeto.entity.Paciente;
import com.elo.saude.dev.projeto.repository.PacienteRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteRepository pacienteRepository;

    public PacienteController(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> listarTodos() {
        return ResponseEntity.ok(pacienteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        return pacienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Paciente> cadastrar(
            @RequestBody Paciente paciente) {

        Paciente salvo = pacienteRepository.save(paciente);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizar(
            @PathVariable Long id,
            @RequestBody Paciente paciente) {

        if (!pacienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        paciente.setId(id);

        return ResponseEntity.ok(pacienteRepository.save(paciente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (!pacienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        pacienteRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
