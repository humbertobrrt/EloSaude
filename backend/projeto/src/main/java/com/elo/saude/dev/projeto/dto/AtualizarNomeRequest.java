package com.elo.saude.dev.projeto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarNomeRequest(
        @NotBlank @Size(max = 150) String nome) {}