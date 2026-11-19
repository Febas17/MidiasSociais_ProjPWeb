package br.edu.faculdade.academico.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "APP", name = "PROFESSOR", uniqueConstraints = {@UniqueConstraint(name = "UQ_PROFESSOR_REGISTRO", columnNames = "REGISTRO"), @UniqueConstraint(name = "UQ_PROFESSOR_EMAIL", columnNames = "EMAIL")})
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "REGISTRO", nullable = false, length = 40)
    private String registro;

    @Column(name = "NOME", nullable = false, length = 200)
    private String nome;

    @Column(name = "EMAIL", length = 200)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEPARTAMENTO_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PROFESSOR_DEPARTAMENTO"))
    private Departamento departamento;

    public Professor() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getRegistro() { return registro; }
    public void setRegistro(String registro) { this.registro = registro; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

}
