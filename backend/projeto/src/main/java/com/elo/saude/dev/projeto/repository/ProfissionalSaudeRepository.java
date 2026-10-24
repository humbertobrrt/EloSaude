package com.elo.saude.dev.projeto.repository;

import com.elo.saude.dev.projeto.entity.ProfissionalSaude;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {

    Optional<ProfissionalSaude> findByUsuarioId(Long usuarioId);

    List<ProfissionalSaude> findByUnidadeId(Long unidadeId);
}