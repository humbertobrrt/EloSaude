
package com.elo.saude.dev.projeto.service;

import com.elo.saude.dev.projeto.entity.ContraReferencia;
import com.elo.saude.dev.projeto.repository.ContraReferenciaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ContraReferenciaService {

    private final ContraReferenciaRepository repository;

    public ContraReferenciaService(
            ContraReferenciaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ContraReferencia> listarTodas() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ContraReferencia buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contrarreferência não encontrada."));
    }

    @Transactional(readOnly = true)
    public ContraReferencia buscarPorEncaminhamento(
            Long encaminhamentoId) {

        return repository.findByEncaminhamentoId(encaminhamentoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contrarreferência não encontrada para este encaminhamento."));
    }

    @Transactional
    public ContraReferencia cadastrar(ContraReferencia contraReferencia) {

        if (contraReferencia.getEncaminhamento() == null
                || contraReferencia.getEncaminhamento().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O encaminhamento é obrigatório.");
        }

        Long encaminhamentoId =
                contraReferencia.getEncaminhamento().getId();

        if (repository.existsByEncaminhamentoId(encaminhamentoId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este encaminhamento já possui uma contrarreferência.");
        }

        return repository.save(contraReferencia);
    }

    @Transactional
    public ContraReferencia atualizar(
            Long id,
            ContraReferencia dadosAtualizados) {

        ContraReferencia existente = buscarPorId(id);

        existente.setEncaminhamento(
                dadosAtualizados.getEncaminhamento());

        existente.setAtendimentoDestino(
                dadosAtualizados.getAtendimentoDestino());

        existente.setProfissional(
                dadosAtualizados.getProfissional());

        existente.setResumoClinico(
                dadosAtualizados.getResumoClinico());

        existente.setPlano(
                dadosAtualizados.getPlano());

        existente.setOrientacoes(
                dadosAtualizados.getOrientacoes());

        existente.setDataRetorno(
                dadosAtualizados.getDataRetorno());

        return repository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        ContraReferencia existente = buscarPorId(id);
        repository.delete(existente);
    }
}
