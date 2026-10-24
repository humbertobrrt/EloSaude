package com.elo.saude.dev.projeto.service;

import com.elo.saude.dev.projeto.entity.Encaminhamento;
import com.elo.saude.dev.projeto.enums.Prioridade;
import com.elo.saude.dev.projeto.enums.StatusEncaminhamento;
import com.elo.saude.dev.projeto.repository.EncaminhamentoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EncaminhamentoService {

    private final EncaminhamentoRepository repository;

    public EncaminhamentoService(
            EncaminhamentoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Encaminhamento> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Encaminhamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Encaminhamento não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Encaminhamento> buscarPorPaciente(Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }

    @Transactional(readOnly = true)
    public List<Encaminhamento> buscarPorUnidadeOrigem(
            Long unidadeId) {
        return repository.findByUnidadeOrigemId(unidadeId);
    }

    @Transactional(readOnly = true)
    public List<Encaminhamento> buscarPorUnidadeDestino(
            Long unidadeId) {
        return repository.findByUnidadeDestinoId(unidadeId);
    }

    @Transactional(readOnly = true)
    public List<Encaminhamento> buscarPorStatus(
            StatusEncaminhamento status) {
        return repository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Encaminhamento> buscarPorPrioridade(
            Prioridade prioridade) {
        return repository.findByPrioridade(prioridade);
    }

    @Transactional
    public Encaminhamento cadastrar(
            Encaminhamento encaminhamento) {

        validarObrigatorios(encaminhamento);

        if (encaminhamento.getId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não informe o ID ao cadastrar um encaminhamento.");
        }

        if (encaminhamento.getUnidadeOrigem().getId()
                .equals(encaminhamento.getUnidadeDestino().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A unidade de origem deve ser diferente da unidade de destino.");
        }

        if (encaminhamento.getPrioridade() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A prioridade é obrigatória.");
        }

        if (encaminhamento.getMotivo() == null
                || encaminhamento.getMotivo().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O motivo é obrigatório.");
        }

        if (encaminhamento.getStatus() == null) {
            encaminhamento.setStatus(StatusEncaminhamento.CRIADO);
        }

        return repository.save(encaminhamento);
    }

    @Transactional
    public Encaminhamento atualizarStatus(
            Long id,
            StatusEncaminhamento novoStatus) {

        if (novoStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O status é obrigatório.");
        }

        Encaminhamento existente = buscarPorId(id);

        existente.setStatus(novoStatus);

        return repository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Encaminhamento existente = buscarPorId(id);
        repository.delete(existente);
    }

    private void validarObrigatorios(
            Encaminhamento encaminhamento) {

        if (encaminhamento.getPaciente() == null
                || encaminhamento.getPaciente().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O paciente é obrigatório.");
        }

        if (encaminhamento.getUnidadeOrigem() == null
                || encaminhamento.getUnidadeOrigem().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A unidade de origem é obrigatória.");
        }

        if (encaminhamento.getUnidadeDestino() == null
                || encaminhamento.getUnidadeDestino().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A unidade de destino é obrigatória.");
        }

        if (encaminhamento.getProfissionalSolicitante() == null
                || encaminhamento.getProfissionalSolicitante().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O profissional solicitante é obrigatório.");
        }
    }
}