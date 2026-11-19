package br.edu.faculdade.academico.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(schema = "APP", name = "DEPARTAMENTO", uniqueConstraints = {@UniqueConstraint(name = "UQ_DEP_NOME", columnNames = "NOME"), @UniqueConstraint(name = "UQ_DEP_SIGLA", columnNames = "SIGLA")})
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
