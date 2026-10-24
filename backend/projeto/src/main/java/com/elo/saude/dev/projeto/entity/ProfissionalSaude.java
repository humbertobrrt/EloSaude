package com.elo.saude.dev.projeto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profissional_saude")
@Getter
@Setter
@NoArgsConstructor
public class ProfissionalSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private UnidadeSaude unidade;

    @Column(name = "tipo_profissional", nullable = false, length = 20)
    private String tipoProfissional;

    @Column(name = "registro_conselho", length = 30)
    private String registroConselho;

    @Column(length = 100)
    private String especialidade;
}
