
package com.elo.saude.dev.projeto.repository;

import com.elo.saude.dev.projeto.entity.ContraReferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContraReferenciaRepository
        extends JpaRepository<ContraReferencia, Long> {

    Optional<ContraReferencia> findByEncaminhamentoId(
            Long encaminhamentoId);

    boolean existsByEncaminhamentoId(Long encaminhamentoId);
}
