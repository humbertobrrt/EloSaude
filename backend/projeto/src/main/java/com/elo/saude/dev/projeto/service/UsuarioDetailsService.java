package com.elo.saude.dev.projeto.service;

import com.elo.saude.dev.projeto.*;
import com.elo.saude.dev.projeto.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email.trim().toLowerCase())
                .map(u -> User.builder()
                        .username(u.getEmail())
                        .password(u.getSenhaHash())
                        .roles(u.getPerfil().name())   // vira ROLE_PACIENTE, ROLE_ADMIN...
                        .disabled(!u.isAtivo())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
