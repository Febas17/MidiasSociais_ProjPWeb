package br.edu.faculdade.academico.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(schema = "APP", name = "DISCENTE",
        uniqueConstraints = @UniqueConstraint(name = "UQ_DISCENTE_MATRICULA", columnNames = "MATRICULA"),
        indexes = {
                @Index(name = "IDX_DISCENTE_NOME", columnList = "NOME"),
                @Index(name = "IDX_DISCENTE_CURSO", columnList = "CURSO_ID")
        })
public class Discente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "MATRICULA", nullable = false, length = 40)
    private String matricula;

    @Column(name = "NOME", nullable = false, length = 200)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CURSO_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_DISCENTE_CURSO"))
    private Curso curso;

    @OneToOne(fetch = FetchType.LAZY, optional = false,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ENDERECO_ID", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "FK_DISCENTE_ENDERECO"))
    private Endereco endereco;

    public Discente() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
}
