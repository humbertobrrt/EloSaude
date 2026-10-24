package com.elo.saude.dev.projeto.service;


import com.elo.saude.dev.projeto.dto.atendimento.SolicitacaoCreateRequest;
import com.elo.saude.dev.projeto.dto.atendimento.SolicitacaoResponse;
import com.elo.saude.dev.projeto.dto.atendimento.SolicitacaoStatusRequest;
import com.elo.saude.dev.projeto.entity.Paciente;
import com.elo.saude.dev.projeto.entity.ProfissionalSaude;
import com.elo.saude.dev.projeto.entity.SolicitacaoAtendimento;
import com.elo.saude.dev.projeto.entity.UnidadeSaude;
import com.elo.saude.dev.projeto.enums.StatusSolicitacao;
import com.elo.saude.dev.projeto.repository.PacienteRepository;
import com.elo.saude.dev.projeto.repository.ProfissionalSaudeRepository;
import com.elo.saude.dev.projeto.repository.SolicitacaoAtendimentoRepository;
import com.elo.saude.dev.projeto.repository.UnidadeSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoAtendimentoRepository repository;
    private final PacienteRepository pacienteRepository;
    private final UnidadeSaudeRepository unidadeRepository;
    private final ProfissionalSaudeRepository profissionalRepository;

    @Transactional
    public SolicitacaoResponse criar(String emailPaciente, SolicitacaoCreateRequest req) {
        Paciente paciente = pacienteDe(emailPaciente);
        UnidadeSaude unidade = unidadeRepository.findById(req.unidadeId())
                .filter(UnidadeSaude::isAtiva)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Unidade não encontrada ou inativa"));
        SolicitacaoAtendimento s = repository.save(
                new SolicitacaoAtendimento(paciente, unidade, req.tipo(), req.motivo().trim()));
        return SolicitacaoResponse.from(s);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponse> listarMinhas(String emailPaciente) {
        Paciente paciente = pacienteDe(emailPaciente);
        return repository.findByPacienteIdOrderByCriadoEmDesc(paciente.getId())
                .stream().map(SolicitacaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponse> listarDaUnidade(String emailProfissional,
                                                     StatusSolicitacao status) {
        Long unidadeId = profissionalDe(emailProfissional).getUnidade().getId();
        List<SolicitacaoAtendimento> lista = (status == null)
                ? repository.findByUnidadeIdOrderByCriadoEmDesc(unidadeId)
                : repository.findByUnidadeIdAndStatusOrderByCriadoEmDesc(unidadeId, status);
        return lista.stream().map(SolicitacaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SolicitacaoResponse buscar(Long id, String email) {
        SolicitacaoAtendimento s = repository.findById(id).orElseThrow(this::naoEncontrada);
        boolean dono = s.getPaciente().getUsuario().getEmail().equalsIgnoreCase(email);
        boolean daUnidade = profissionalRepository.findByUsuarioEmail(email)
                .map(p -> p.getUnidade().getId().equals(s.getUnidade().getId()))
                .orElse(false);
        if (!dono && !daUnidade) throw naoEncontrada();
        return SolicitacaoResponse.from(s);
    }

    @Transactional
    public SolicitacaoResponse atualizarStatus(Long id, String emailProfissional,
                                               SolicitacaoStatusRequest req) {
        ProfissionalSaude prof = profissionalDe(emailProfissional);
        SolicitacaoAtendimento s = repository.findById(id)
                .filter(x -> x.getUnidade().getId().equals(prof.getUnidade().getId()))
                .orElseThrow(this::naoEncontrada);

        if (s.encerrada()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação já encerrada");
        }
        if (req.status() == StatusSolicitacao.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível voltar a solicitação para PENDENTE");
        }
        if (req.status() == StatusSolicitacao.CONFIRMADA && req.dataPrevista() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe a data prevista para confirmar");
        }
        s.atualizarStatus(req.status(), req.respostaUnidade(), req.dataPrevista());
        return SolicitacaoResponse.from(s);
    }

    @Transactional
    public SolicitacaoResponse cancelar(Long id, String emailPaciente) {
        Paciente paciente = pacienteDe(emailPaciente);
        SolicitacaoAtendimento s = repository.findById(id)
                .filter(x -> x.getPaciente().getId().equals(paciente.getId()))
                .orElseThrow(this::naoEncontrada);
        if (s.encerrada()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação já encerrada");
        }
        s.cancelar();
        return SolicitacaoResponse.from(s);
    }

    private Paciente pacienteDe(String email) {
        return pacienteRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Seu usuário não possui cadastro de paciente"));
    }

    private ProfissionalSaude profissionalDe(String email) {
        return profissionalRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Seu usuário não possui cadastro de profissional de saúde"));
    }

    private ResponseStatusException naoEncontrada() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada");
    }
}
