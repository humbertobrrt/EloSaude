package com.elo.saude.dev.projeto.dto.atendimento;

import com.elo.saude.dev.projeto.enums.StatusSolicitacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SolicitacaoStatusRequest(
        @NotNull StatusSolicitacao status,
        @Size(max = 500) String respostaUnidade,
        LocalDateTime dataPrevista) {}
