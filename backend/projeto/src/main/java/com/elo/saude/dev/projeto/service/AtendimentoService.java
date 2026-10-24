package com.elo.saude.dev.projeto.service;

import com.elo.saude.dev.projeto.dto.atendimento.AtendimentoCreateRequest;
import com.elo.saude.dev.projeto.dto.atendimento.AtendimentoResponse;
import com.elo.saude.dev.projeto.entity.Atendimento;
import com.elo.saude.dev.projeto.entity.Paciente;
import com.elo.saude.dev.projeto.entity.ProfissionalSaude;
import com.elo.saude.dev.projeto.entity.SolicitacaoAtendimento;
import com.elo.saude.dev.projeto.repository.AtendimentoRepository;
import com.elo.saude.dev.projeto.repository.PacienteRepository;
import com.elo.saude.dev.projeto.repository.ProfissionalSaudeRepository;
import com.elo.saude.dev.projeto.repository.SolicitacaoAtendimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AtendimentoService {

    private final AtendimentoRepository repository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalRepository;
    private final SolicitacaoAtendimentoRepository solicitacaoRepository;

    @Transactional
    public AtendimentoResponse registrar(String emailProfissional, AtendimentoCreateRequest req) {
        ProfissionalSaude prof = profissionalDe(emailProfissional);
        Paciente paciente = pacienteRepository.findById(req.pacienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paciente não encontrado"));

        SolicitacaoAtendimento solicitacao = null;
        if (req.solicitacaoId() != null) {
            solicitacao = solicitacaoRepository.findById(req.solicitacaoId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Solicitação não encontrada"));
            boolean mesmaUnidade = solicitacao.getUnidade().getId().equals(prof.getUnidade().getId());
            boolean mesmoPaciente = solicitacao.getPaciente().getId().equals(paciente.getId());
            if (!mesmaUnidade || !mesmoPaciente) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A solicitação não pertence a este paciente e a esta unidade");
            }
        }

        Atendimento atendimento = repository.save(new Atendimento(
                paciente, prof, solicitacao,
                req.queixaPrincipal().trim(), req.anamnese(), req.diagnostico(), req.cid10(),
                req.conduta(), req.prescricao(), req.observacoes()));

        // Atendimento registrado conclui a solicitação de origem
        if (solicitacao != null && !solicitacao.encerrada()) {
            solicitacao.concluir();
        }
        return AtendimentoResponse.from(atendimento);
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponse> meuHistorico(String emailPaciente) {
        Paciente paciente = pacienteRepository.findByUsuarioEmail(emailPaciente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Seu usuário não possui cadastro de paciente"));
        return repository.findByPacienteIdOrderByDataHoraDesc(paciente.getId())
                .stream().map(AtendimentoResponse::from).toList();
    }

    // Por enquanto, só atendimentos da própria unidade do profissional.
    // Quando existir Encaminhamento, incluir os compartilhados.
    @Transactional(readOnly = true)
    public List<AtendimentoResponse> historicoDoPaciente(String emailProfissional, Long pacienteId) {
        ProfissionalSaude prof = profissionalDe(emailProfissional);
        return repository.findByPacienteIdAndUnidadeIdOrderByDataHoraDesc(
                        pacienteId, prof.getUnidade().getId())
                .stream().map(AtendimentoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AtendimentoResponse buscar(Long id, String email) {
        Atendimento a = repository.findById(id).orElseThrow(this::naoEncontrado);
        boolean dono = a.getPaciente().getUsuario().getEmail().equalsIgnoreCase(email);
        boolean daUnidade = profissionalRepository.findByUsuarioEmail(email)
                .map(p -> p.getUnidade().getId().equals(a.getUnidade().getId()))
                .orElse(false);
        if (!dono && !daUnidade) throw naoEncontrado();
        return AtendimentoResponse.from(a);
    }

    private ProfissionalSaude profissionalDe(String email) {
        return profissionalRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Seu usuário não possui cadastro de profissional de saúde"));
    }

    private ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Atendimento não encontrado");
    }
}
