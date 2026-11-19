package br.edu.faculdade.bd.jpa.model;

import jakarta.persistence.*;

@Entity
@Table(schema = "APP", name = "DEPARTAMENTO",
        uniqueConstraints = {
            @UniqueConstraint(name = "UQ_DEP_NOME", columnNames = "NOME"),
            @UniqueConstraint(name = "UQ_DEP_SIGLA", columnNames = "SIGLA")
        },
        indexes = {
            @Index(name = "IDX_DEP_SIGLA", columnList = "SIGLA")
        })
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DEPARTAMENTO")
    private Integer id;

    @Column(name = "NOME", nullable = false, length = 200)
    private String nome;

    @Column(name = "SIGLA", nullable = false, length = 10)
    private String sigla;

    public Departamento() {
    }

    public Departamento(String nome, String sigla) {
        this.nome = nome;
        this.sigla = sigla;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
