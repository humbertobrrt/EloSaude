package com.elo.saude.dev.projeto.dto.atendimento;

import com.elo.saude.dev.projeto.entity.Atendimento;

import java.time.LocalDateTime;

public record AtendimentoResponse(
        Long id,
        Long pacienteId, String pacienteNome,
        Long profissionalId, String profissionalNome,
        Long unidadeId, String unidadeNome,
        Long solicitacaoId, Long encaminhamentoId,
        LocalDateTime dataHora,
        String queixaPrincipal, String anamnese, String diagnostico, String cid10,
        String conduta, String prescricao, String observacoes) {

    public static AtendimentoResponse from(Atendimento a) {
        return new AtendimentoResponse(
                a.getId(),
                a.getPaciente().getId(), a.getPaciente().getUsuario().getNome(),
                a.getProfissional().getId(), a.getProfissional().getUsuario().getNome(),
                a.getUnidade().getId(), a.getUnidade().getNome(),
                a.getSolicitacao() != null ? a.getSolicitacao().getId() : null,
                a.getEncaminhamentoId(),
                a.getDataHora(),
                a.getQueixaPrincipal(), a.getAnamnese(), a.getDiagnostico(), a.getCid10(),
                a.getConduta(), a.getPrescricao(), a.getObservacoes());
    }
}
