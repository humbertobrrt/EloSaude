
package com.elo.saude.dev.projeto.repository;

import com.elo.saude.dev.projeto.entity.Encaminhamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncaminhamentoRepository
        extends JpaRepository<Encaminhamento, Long> {

    List<Encaminhamento> findByPacienteId(Long pacienteId);

    List<Encaminhamento> findByUnidadeOrigemId(Long unidadeId);

    List<Encaminhamento> findByUnidadeDestinoId(Long unidadeId);

    List<Encaminhamento> findByStatus(
            Encaminhamento.StatusEncaminhamento status);

    List<Encaminhamento> findByPrioridade(
            Encaminhamento.Prioridade prioridade);
}
