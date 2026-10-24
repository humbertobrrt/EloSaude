package com.elo.saude.dev.projeto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


import com.elo.saude.dev.projeto.enums.*;
import lombok.AccessLevel;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "unidade_saude")
public class UnidadeSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUnidade tipo;

    @Column(length = 10)
    private String cnes;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false)
    private boolean ativa = true;
}
