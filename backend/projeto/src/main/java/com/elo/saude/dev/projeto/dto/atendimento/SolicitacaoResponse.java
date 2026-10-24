package com.elo.saude.dev.projeto.dto.atendimento;

import com.elo.saude.dev.projeto.entity.SolicitacaoAtendimento;
import com.elo.saude.dev.projeto.enums.StatusSolicitacao;
import com.elo.saude.dev.projeto.enums.TipoSolicitacao;

import java.time.LocalDateTime;

public record SolicitacaoResponse(
        Long id, String protocolo,
        Long pacienteId, String pacienteNome,
        Long unidadeId, String unidadeNome,
        TipoSolicitacao tipo, String motivo, StatusSolicitacao status,
        String respostaUnidade, LocalDateTime dataPrevista,
        LocalDateTime criadoEm, LocalDateTime atualizadoEm) {

    public static SolicitacaoResponse from(SolicitacaoAtendimento s) {
        String protocolo = "ELO-%d-%06d".formatted(s.getCriadoEm().getYear(), s.getId());
        return new SolicitacaoResponse(
                s.getId(), protocolo,
                s.getPaciente().getId(), s.getPaciente().getUsuario().getNome(),
                s.getUnidade().getId(), s.getUnidade().getNome(),
                s.getTipo(), s.getMotivo(), s.getStatus(),
                s.getRespostaUnidade(), s.getDataPrevista(),
                s.getCriadoEm(), s.getAtualizadoEm());
    }
}
