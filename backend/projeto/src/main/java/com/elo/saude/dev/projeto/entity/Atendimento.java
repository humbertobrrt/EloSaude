package com.elo.saude.dev.projeto.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "atendimento")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profissional_id", nullable = false)
    private ProfissionalSaude profissional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private UnidadeSaude unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id")
    private SolicitacaoAtendimento solicitacao;

    // Vira relacionamento quando a entidade Encaminhamento existir
    @Column(name = "encaminhamento_id")
    private Long encaminhamentoId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "queixa_principal", length = 500)
    private String queixaPrincipal;

    @Column(columnDefinition = "text")
    private String anamnese;

    @Column(length = 500)
    private String diagnostico;

    @Column(length = 10)
    private String cid10;

    @Column(columnDefinition = "text")
    private String conduta;

    @Column(columnDefinition = "text")
    private String prescricao;

    @Column(columnDefinition = "text")
    private String observacoes;

    public Atendimento(Paciente paciente, ProfissionalSaude profissional,
                       SolicitacaoAtendimento solicitacao, String queixaPrincipal,
                       String anamnese, String diagnostico, String cid10,
                       String conduta, String prescricao, String observacoes) {
        this.paciente = paciente;
        this.profissional = profissional;
        this.unidade = profissional.getUnidade();
        this.solicitacao = solicitacao;
        this.queixaPrincipal = queixaPrincipal;
        this.anamnese = anamnese;
        this.diagnostico = diagnostico;
        this.cid10 = cid10;
        this.conduta = conduta;
        this.prescricao = prescricao;
        this.observacoes = observacoes;
    }

    @PrePersist
    void prePersist() {
        if (dataHora == null) dataHora = LocalDateTime.now();
    }
}
