package com.elo.saude.dev.projeto.dto.atendimento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtendimentoCreateRequest(
        @NotNull Long pacienteId,
        Long solicitacaoId,
        @NotBlank @Size(max = 500) String queixaPrincipal,
        String anamnese,
        @Size(max = 500) String diagnostico,
        @Size(max = 10) String cid10,
        String conduta,
        String prescricao,
        String observacoes) {}
