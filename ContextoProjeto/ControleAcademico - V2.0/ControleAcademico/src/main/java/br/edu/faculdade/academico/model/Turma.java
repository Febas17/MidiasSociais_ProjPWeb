package br.edu.faculdade.academico.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "APP", name = "TURMA", uniqueConstraints = {@UniqueConstraint(name = "UQ_TURMA_CODIGO_PERIODO", columnNames = {"CODIGO", "ANO", "SEMESTRE"})}, indexes = {@Index(name = "IDX_TURMA_DISCIPLINA", columnList = "DISCIPLINA_ID"), @Index(name = "IDX_TURMA_PROFESSOR", columnList = "PROFESSOR_ID")})
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "CODIGO", nullable = false, length = 30)
    private String codigo;

    @Column(name = "ANO", nullable = false)
    private Integer ano;

    @Column(name = "SEMESTRE", nullable = false)
    private Integer semestre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DISCIPLINA_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TURMA_DISCIPLINA"))
    private Disciplina disciplina;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROFESSOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TURMA_PROFESSOR"))
    private Professor professor;

    public Turma() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }

    public Disciplina getDisciplina() { return disciplina; }
    public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }

    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }

}
