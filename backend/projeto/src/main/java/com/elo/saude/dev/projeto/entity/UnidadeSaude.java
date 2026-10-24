package com.elo.saude.dev.projeto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unidade_saude")
@Getter
@Setter
@NoArgsConstructor
public class UnidadeSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(length = 10, unique = true)
    private String cnes;

    @Column(length = 200)
    private String endereco;

    @Column(length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(length = 20)
    private String telefone;

    @Column(length = 500)
    private String especialidades;

    @Column(nullable = false)
    private Boolean ativa = true;
}
