package com.elo.saude.dev.projeto.service;

import com.elo.saude.dev.projeto.entity.Usuario;
import com.elo.saude.dev.projeto.enuns.Perfil;
import com.elo.saude.dev.projeto.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario criar(String nome, String email, String senha, Perfil perfil) {
        String emailNormalizado = email.trim().toLowerCase();
        if (repository.existsByEmail(emailNormalizado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        Usuario u = new Usuario(
                nome.trim(),
                emailNormalizado,
                passwordEncoder.encode(senha),
                perfil);
        return repository.save(u);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return repository.findByEmail(email.trim().toLowerCase()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    @Transactional
    public Usuario atualizarNome(String email, String novoNome) {
        Usuario u = buscarPorEmail(email);
        u.alterarNome(novoNome.trim());
        return u;
    }

    @Transactional
    public Usuario alterarAtivo(Long id, boolean ativo) {
        Usuario u = buscarPorId(id);
        u.alterarAtivo(ativo);
        return u;
    }
}
