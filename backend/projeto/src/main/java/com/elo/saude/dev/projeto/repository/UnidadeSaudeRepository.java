package com.elo.saude.dev.projeto.repository;

import com.elo.saude.dev.projeto.entity.UnidadeSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnidadeSaudeRepository
        extends JpaRepository<UnidadeSaude, Long> {

    boolean existsByCnes(String cnes);

    Optional<UnidadeSaude> findByCnes(String cnes);
}

