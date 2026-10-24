package com.elo.saude.dev.projeto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contra_referencia")
public class ContraReferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encaminhamento_id", nullable = false, unique = true)
    private Encaminhamento encaminhamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendimento_destino_id")
    private Atendimento atendimentoDestino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profissional_id", nullable = false)
    private ProfissionalSaude profissional;

    @Column(name = "resumo_atendimento", nullable = false, columnDefinition = "text")
    private String resumoClinico;

    @Column(name = "plano_cuidado", columnDefinition = "text")
    private String plano;

    @Column(name = "orientacoes_unidade_origem", columnDefinition = "text")
    private String orientacoes;

    @Column(name = "retorno_recomendado_em")
    private LocalDate dataRetorno;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }
}