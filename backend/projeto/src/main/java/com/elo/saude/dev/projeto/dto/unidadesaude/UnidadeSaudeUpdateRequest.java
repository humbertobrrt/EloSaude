package com.elo.saude.dev.projeto.dto.unidadesaude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UnidadeSaudeUpdateRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nome,

        @NotBlank(message = "O tipo é obrigatório")
        @Size(max = 20)
        String tipo,

        @Size(max = 10)
        String cnes,

        @Size(max = 200)
        String endereco,

        @Size(max = 100)
        String bairro,

        @NotBlank(message = "A cidade é obrigatória")
        @Size(max = 100)
        String cidade,

        @NotBlank(message = "A UF é obrigatória")
        @Pattern(regexp = "[A-Za-z]{2}",
                message = "A UF deve conter duas letras")
        String uf,

        @Size(max = 20)
        String telefone,

        @Size(max = 500)
        String especialidades

) {}

