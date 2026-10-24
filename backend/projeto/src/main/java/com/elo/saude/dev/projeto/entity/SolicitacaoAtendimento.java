package com.elo.saude.dev.projeto.entity;

import com.elo.saude.dev.projeto.enums.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "solicitacao_atendimento")
public class SolicitacaoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private UnidadeSaude unidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoSolicitacao tipo;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacao status = StatusSolicitacao.PENDENTE;

    @Column(name = "resposta_unidade", length = 500)
    private String respostaUnidade;

    @Column(name = "data_prevista")
    private LocalDateTime dataPrevista;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public SolicitacaoAtendimento(Paciente paciente, UnidadeSaude unidade,
                                  TipoSolicitacao tipo, String motivo) {
        this.paciente = paciente;
        this.unidade = unidade;
        this.tipo = tipo;
        this.motivo = motivo;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        criadoEm = agora;
        atualizadoEm = agora;
    }

    @PreUpdate
    void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public boolean encerrada() {
        return status == StatusSolicitacao.CONCLUIDA || status == StatusSolicitacao.CANCELADA;
    }

    public void atualizarStatus(StatusSolicitacao novoStatus, String resposta,
                                LocalDateTime previsao) {
        this.status = novoStatus;
        if (resposta != null && !resposta.isBlank()) this.respostaUnidade = resposta.trim();
        if (previsao != null) this.dataPrevista = previsao;
    }

    public void concluir() {
        this.status = StatusSolicitacao.CONCLUIDA;
    }

    public void cancelar() {
        this.status = StatusSolicitacao.CANCELADA;
    }
}
