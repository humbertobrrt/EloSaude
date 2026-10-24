package com.elo.saude.dev.projeto.dto.unidadesaude;

import com.elo.saude.dev.projeto.enums.TipoUnidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UnidadeSaudeCreateRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotNull TipoUnidade tipo,
        @Size(max = 10) String cnes,
        @Size(max = 200) String endereco,
        @Size(max = 100) String bairro,
        @NotBlank @Size(max = 100) String cidade,
        @NotBlank @Pattern(regexp = "[A-Za-z]{2}", message = "UF deve ter 2 letras") String uf,
        @Size(max = 20) String telefone,
        @Size(max = 500) String especialidades) {}