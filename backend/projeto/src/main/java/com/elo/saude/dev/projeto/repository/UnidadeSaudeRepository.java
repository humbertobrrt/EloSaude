package com.elo.saude.dev.projeto.repository;

import com.elo.saude.dev.projeto.entity.UnidadeSaude;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnidadeSaudeRepository extends JpaRepository<UnidadeSaude, Long> {
    Optional<UnidadeSaude> findByCnes(String cnes);
    boolean existsByCnes(String cnes);
}
