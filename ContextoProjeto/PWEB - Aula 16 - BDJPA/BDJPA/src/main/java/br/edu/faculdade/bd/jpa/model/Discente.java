package br.edu.faculdade.bd.jpa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DISCENTE",
        uniqueConstraints = {
            @UniqueConstraint(name = "UQ_DISCENTE_MATRICULA", columnNames = {"MATRICULA"})
        },
        indexes = {
            @Index(name = "IDX_DISCENTE_NOME", columnList = "NOME"),
            @Index(name = "IDX_DISCENTE_CURSO", columnList = "CURSO_ID")
        })
public class Discente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "MATRICULA", nullable = false, length = 40)
    private String matricula;

    @Column(name = "NOME", nullable = false, length = 200)
    private String nome;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CURSO_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_DISCENTE_CURSO"))
    private Curso curso;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

}
