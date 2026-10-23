package com.elo.saude.dev.projeto.dto;

public record LoginResponse(
        String token, String tipo, long expiraEmSegundos, UsuarioResponse usuario) {}