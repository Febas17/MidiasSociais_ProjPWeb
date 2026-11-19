package br.edu.faculdade.academico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
    schema = "APP",
    name = "DISCIPLINA",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UQ_DISCIPLINA_CODIGO",
            columnNames = "CODIGO"
        )
    },
    indexes = {
        @Index(
            name = "IDX_DISCIPLINA_CURSO",
            columnList = "CURSO_ID"
        ),
        @Index(
            name = "IDX_DISCIPLINA_CURSO_SEMESTRE",
            columnList = "CURSO_ID, SEMESTRE"
        )
    }
)
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "CODIGO", nullable = false, length = 30)
    private String codigo;

    @Column(name = "NOME", nullable = false, length = 150)
    private String nome;

    @Column(name = "CARGA_HORARIA", nullable = false)
    private Integer cargaHoraria;

    // Semestre da disciplina na matriz curricular do curso (1 a 12 no arquivo).
    // Não confundir com Turma.semestre, que representa o período de oferta.
    @Column(name = "SEMESTRE", nullable = false)
    private Integer semestre;

    // Proveniência da matriz: por exemplo, PROPOSTA_DIDATICA.
    @Column(name = "TIPO_DADO", nullable = false, length = 30)
    private String tipoDado = "PROPOSTA_DIDATICA";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "CURSO_ID",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_DISCIPLINA_CURSO")
    )
    private Curso curso;

    public Disciplina() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public String getTipoDado() {
        return tipoDado;
    }

    public void setTipoDado(String tipoDado) {
        this.tipoDado = tipoDado;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}
