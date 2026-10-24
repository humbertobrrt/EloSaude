package com.elo.saude.dev.projeto.dto.unidadesaude;

import com.elo.saude.dev.projeto.entity.UnidadeSaude;

public record UnidadeSaudeResponse(
        Long id,
        String nome,
        String tipo,
        String cnes,
        String endereco,
        String bairro,
        String cidade,
        String uf,
        String telefone,
        String especialidades,
        Boolean ativa
) {

    public static UnidadeSaudeResponse from(UnidadeSaude unidade) {
        return new UnidadeSaudeResponse(
                unidade.getId(),
                unidade.getNome(),
                unidade.getTipo().name(),
                unidade.getCnes(),
                unidade.getEndereco(),
                unidade.getBairro(),
                unidade.getCidade(),
                unidade.getUf(),
                unidade.getTelefone(),
                unidade.getEspecialidades(),
                unidade.isAtiva()
        );
    }
}

