package com.elo.saude.dev.projeto.controller;

import com.elo.saude.dev.projeto.dto.LoginRequest;
import com.elo.saude.dev.projeto.dto.LoginResponse;
import com.elo.saude.dev.projeto.dto.UsuarioResponse;
import com.elo.saude.dev.projeto.security.TokenService;
import com.elo.saude.dev.projeto.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email().trim().toLowerCase(), req.senha()));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }
        var usuario = usuarioService.buscarPorEmail(req.email());
        return new LoginResponse(tokenService.gerar(usuario), "Bearer",
                tokenService.validadeEmSegundos(), UsuarioResponse.from(usuario));
    }
}
