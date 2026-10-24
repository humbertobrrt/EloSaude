package com.elo.saude.dev.projeto.repository;

import com.elo.saude.dev.projeto.entity.Atendimento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    @EntityGraph(attributePaths = {"paciente.usuario", "profissional.usuario", "unidade"})
    List<Atendimento> findByPacienteIdOrderByDataHoraDesc(Long pacienteId);

    @EntityGraph(attributePaths = {"paciente.usuario", "profissional.usuario", "unidade"})
    List<Atendimento> findByPacienteIdAndUnidadeIdOrderByDataHoraDesc(
            Long pacienteId, Long unidadeId);
}
