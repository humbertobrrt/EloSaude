package com.elo.saude.dev.projeto.dto.atendimento;

import com.elo.saude.dev.projeto.enums.TipoSolicitacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitacaoCreateRequest(
        @NotNull Long unidadeId,
        @NotNull TipoSolicitacao tipo,
        @NotBlank @Size(max = 500) String motivo) {}
