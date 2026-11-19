package br.edu.faculdade.academico.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "APP", name = "CURSO", uniqueConstraints = {@UniqueConstraint(name = "UQ_CURSO_NOME_DEP", columnNames = {"NOME", "DEPARTAMENTO_ID"}), @UniqueConstraint(name = "UQ_CURSO_SIGLA", columnNames = "SIGLA")}, indexes = {@Index(name = "IDX_CURSO_DEP", columnList = "DEPARTAMENTO_ID")})
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "SIGLA", nullable = false, length = 20)
    private String sigla;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEPARTAMENTO_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CURSO_DEPARTAMENTO"))
    private Departamento departamento;

    public Curso() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

}
