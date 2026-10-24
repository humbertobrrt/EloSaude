package com.elo.saude.dev.projeto.repository;

import com.elo.saude.dev.projeto.*;

import com.elo.saude.dev.projeto.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    Optional<Paciente> findByUsuarioEmail(String email);

    Optional<Paciente> findByUsuarioId(Long usuarioId);
}
