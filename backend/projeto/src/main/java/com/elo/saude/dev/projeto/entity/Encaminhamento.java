package com.elo.saude.dev.projeto.entity;

import com.elo.saude.dev.projeto.enums.Prioridade;
import com.elo.saude.dev.projeto.enums.StatusEncaminhamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "encaminhamento")
public class Encaminhamento {

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

    @Column(name = "especialidade_destino", length = 100)
    private String especialidadeDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridade prioridade = Prioridade.MEDIA;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(name = "resumo_clinico", columnDefinition = "text")
    private String resumoClinico;

    @Column(name = "compartilhar_historico", nullable = false)
    private boolean compartilharHistorico = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEncaminhamento status = StatusEncaminhamento.CRIADO;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

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
}