package com.elo.saude.dev.projeto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "paciente")
@Getter
@Setter
@NoArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @Column(name = "cartao_sus", length = 15, unique = true)
    private String cartaoSus;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(length = 15)
    private String sexo;

    @Column(length = 20)
    private String telefone;

    @Column(length = 200)
    private String endereco;

    @Column(length = 100)
    private String bairro;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @ManyToOne
    @JoinColumn(name = "ubs_referencia_id")
    private UnidadeSaude ubsReferencia;

    @Column(name = "condicoes_cronicas", length = 500)
    private String condicoesCronicas;

    @Column(length = 500)
    private String alergias;
}
