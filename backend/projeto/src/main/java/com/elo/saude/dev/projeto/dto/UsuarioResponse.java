package com.elo.saude.dev.projeto.dto;

import com.elo.saude.dev.projeto.entity.Usuario;
import com.elo.saude.dev.projeto.enums.Perfil;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id, String nome, String email, Perfil perfil,
        boolean ativo, LocalDateTime criadoEm) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(),
                u.getPerfil(), u.isAtivo(), u.getCriadoEm());
    }
}