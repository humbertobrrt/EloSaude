package com.elo.saude.dev.projeto.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "encaminhamento")
public class Encaminhamento {

    public enum Prioridade {
        BAIXA,
        MEDIA,
        ALTA,
        URGENTE
    }

    public enum StatusEncaminhamento {
        CRIADO,
        ENVIADO,
        RECEBIDO,
        EM_ATENDIMENTO,
        CONCLUIDO,
        CANCELADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendimento_origem_id")
    private Atendimento atendimentoOrigem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_origem_id", nullable = false)
    private UnidadeSaude unidadeOrigem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_destino_id", nullable = false)
    private UnidadeSaude unidadeDestino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profissional_solicitante_id", nullable = false)
    private ProfissionalSaude profissionalSolicitante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEncaminhamento status = StatusEncaminhamento.CRIADO;

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em", insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    public Encaminhamento() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Atendimento getAtendimentoOrigem() {
        return atendimentoOrigem;
    }

    public void setAtendimentoOrigem(Atendimento atendimentoOrigem) {
        this.atendimentoOrigem = atendimentoOrigem;
    }

    public UnidadeSaude getUnidadeOrigem() {
        return unidadeOrigem;
    }

    public void setUnidadeOrigem(UnidadeSaude unidadeOrigem) {
        this.unidadeOrigem = unidadeOrigem;
    }

    public UnidadeSaude getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(UnidadeSaude unidadeDestino) {
        this.unidadeDestino = unidadeDestino;
    }

    public ProfissionalSaude getProfissionalSolicitante() {
        return profissionalSolicitante;
    }

    public void setProfissionalSolicitante(
            ProfissionalSaude profissionalSolicitante) {
        this.profissionalSolicitante = profissionalSolicitante;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public StatusEncaminhamento getStatus() {
        return status;
    }

    public void setStatus(StatusEncaminhamento status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}

