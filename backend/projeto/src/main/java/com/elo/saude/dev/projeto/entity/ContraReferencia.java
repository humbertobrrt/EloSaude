package com.elo.saude.dev.projeto.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contra_referencia")
public class ContraReferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "encaminhamento_id",
            nullable = false,
            unique = true
    )
    private Encaminhamento encaminhamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendimento_destino_id")
    private Atendimento atendimentoDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    @Column(name = "resumo_clinico", columnDefinition = "TEXT")
    private String resumoClinico;

    @Column(name = "plano", columnDefinition = "TEXT")
    private String plano;

    @Column(name = "orientacoes", columnDefinition = "TEXT")
    private String orientacoes;

    @Column(name = "data_retorno")
    private LocalDate dataRetorno;

    public ContraReferencia() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Encaminhamento getEncaminhamento() {
        return encaminhamento;
    }

    public void setEncaminhamento(Encaminhamento encaminhamento) {
        this.encaminhamento = encaminhamento;
    }

    public Atendimento getAtendimentoDestino() {
        return atendimentoDestino;
    }

    public void setAtendimentoDestino(Atendimento atendimentoDestino) {
        this.atendimentoDestino = atendimentoDestino;
    }

    public ProfissionalSaude getProfissional() {
        return profissional;
    }

    public void setProfissional(ProfissionalSaude profissional) {
        this.profissional = profissional;
    }

    public String getResumoClinico() {
        return resumoClinico;
    }

    public void setResumoClinico(String resumoClinico) {
        this.resumoClinico = resumoClinico;
    }

    public String getPlano() {
        return plano;
    }

    public void setPlano(String plano) {
        this.plano = plano;
    }

    public String getOrientacoes() {
        return orientacoes;
    }

    public void setOrientacoes(String orientacoes) {
        this.orientacoes = orientacoes;
    }

    public LocalDate getDataRetorno() {
        return dataRetorno;
    }

    public void setDataRetorno(LocalDate dataRetorno) {
        this.dataRetorno = dataRetorno;
    }
}
