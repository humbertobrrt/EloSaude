package com.elo.saude.dev.projeto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


import com.elo.saude.dev.projeto.enums.*;
import lombok.AccessLevel;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profissional_saude")
public class ProfissionalSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private UnidadeSaude unidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_profissional", nullable = false, length = 20)
    private TipoProfissional tipoProfissional;

    @Column(length = 100)
    private String especialidade;
}
