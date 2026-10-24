package com.elo.saude.dev.projeto.repository;

import com.elo.saude.dev.projeto.entity.SolicitacaoAtendimento;
import com.elo.saude.dev.projeto.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoAtendimentoRepository
        extends JpaRepository<SolicitacaoAtendimento, Long> {

    @EntityGraph(attributePaths = {"paciente.usuario", "unidade"})
    List<SolicitacaoAtendimento> findByPacienteIdOrderByCriadoEmDesc(Long pacienteId);

    @EntityGraph(attributePaths = {"paciente.usuario", "unidade"})
    List<SolicitacaoAtendimento> findByUnidadeIdOrderByCriadoEmDesc(Long unidadeId);

    @EntityGraph(attributePaths = {"paciente.usuario", "unidade"})
    List<SolicitacaoAtendimento> findByUnidadeIdAndStatusOrderByCriadoEmDesc(
            Long unidadeId, StatusSolicitacao status);
}