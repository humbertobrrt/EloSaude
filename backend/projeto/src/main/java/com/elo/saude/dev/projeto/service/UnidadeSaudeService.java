package com.elo.saude.dev.projeto.service;

import com.elo.saude.dev.projeto.dto.unidadesaude.UnidadeSaudeCreateRequest;
import com.elo.saude.dev.projeto.dto.unidadesaude.UnidadeSaudeUpdateRequest;
import com.elo.saude.dev.projeto.entity.UnidadeSaude;
import com.elo.saude.dev.projeto.repository.UnidadeSaudeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UnidadeSaudeService {

    private final UnidadeSaudeRepository repository;

    public UnidadeSaudeService(UnidadeSaudeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UnidadeSaude criar(UnidadeSaudeCreateRequest req) {
        String cnes = normalizarCnes(req.cnes());

        validarCnesDisponivel(cnes);

        UnidadeSaude unidade = new UnidadeSaude();

        unidade.setNome(req.nome().trim());
        unidade.setTipo(req.tipo());
        unidade.setCnes(cnes);
        unidade.setEndereco(req.endereco());
        unidade.setBairro(req.bairro());
        unidade.setCidade(req.cidade().trim());
        unidade.setUf(req.uf().trim().toUpperCase());
        unidade.setTelefone(req.telefone());
        unidade.setEspecialidades(req.especialidades());
        unidade.setAtiva(true);

        return repository.save(unidade);
    }

    @Transactional(readOnly = true)
    public List<UnidadeSaude> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public UnidadeSaude buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unidade de saúde não encontrada"
                ));
    }

    @Transactional(readOnly = true)
    public UnidadeSaude buscarPorCnes(String cnes) {
        String cnesNormalizado = normalizarCnes(cnes);

        if (cnesNormalizado == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O CNES deve ser informado"
            );
        }

        return repository.findByCnes(cnesNormalizado).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unidade de saúde não encontrada"
                ));
    }

    @Transactional
    public UnidadeSaude atualizar(
            Long id,
            UnidadeSaudeUpdateRequest req) {

        UnidadeSaude unidade = buscarPorId(id);

        String novoCnes = normalizarCnes(req.cnes());

        validarCnesDisponivelParaAtualizacao(novoCnes, unidade);

        unidade.setNome(req.nome().trim());
        unidade.setTipo(req.tipo());
        unidade.setCnes(novoCnes);
        unidade.setEndereco(req.endereco());
        unidade.setBairro(req.bairro());
        unidade.setCidade(req.cidade().trim());
        unidade.setUf(req.uf().trim().toUpperCase());
        unidade.setTelefone(req.telefone());
        unidade.setEspecialidades(req.especialidades());

        return repository.save(unidade);
    }

    @Transactional
    public UnidadeSaude alterarAtiva(Long id, boolean ativa) {
        UnidadeSaude unidade = buscarPorId(id);
        unidade.setAtiva(ativa);
        return unidade;
    }

    @Transactional
    public void excluir(Long id) {
        UnidadeSaude unidade = buscarPorId(id);
        repository.delete(unidade);
    }

    private String normalizarCnes(String cnes) {
        if (cnes == null || cnes.isBlank()) {
            return null;
        }

        return cnes.trim();
    }

    private void validarCnesDisponivel(String cnes) {
        if (cnes != null && repository.existsByCnes(cnes)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "CNES já cadastrado"
            );
        }
    }

    private void validarCnesDisponivelParaAtualizacao(
            String cnes,
            UnidadeSaude unidadeAtual) {

        if (cnes != null
                && !cnes.equals(unidadeAtual.getCnes())
                && repository.existsByCnes(cnes)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "CNES já cadastrado"
            );
        }
    }
}