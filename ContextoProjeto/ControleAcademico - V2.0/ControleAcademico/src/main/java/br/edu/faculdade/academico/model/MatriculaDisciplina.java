package br.edu.faculdade.academico.model;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(schema = "APP", name = "MATRICULA_DISCIPLINA", uniqueConstraints = {@UniqueConstraint(name = "UQ_MATDISC_DISCENTE_TURMA", columnNames = {"DISCENTE_ID", "TURMA_ID"})}, indexes = {@Index(name = "IDX_MATDISC_TURMA", columnList = "TURMA_ID")})
public class MatriculaDisciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DISCENTE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_MATDISC_DISCENTE"))
    private Discente discente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TURMA_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_MATDISC_TURMA"))
    private Turma turma;

    @Column(name = "DATA_MATRICULA", nullable = false)
    private LocalDate dataMatricula;

    @Column(name = "NOTA_FINAL", precision = 4, scale = 2)
    private BigDecimal notaFinal;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false, length = 20)
    private SituacaoMatricula situacao;

    public MatriculaDisciplina() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Discente getDiscente() { return discente; }
    public void setDiscente(Discente discente) { this.discente = discente; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    public LocalDate getDataMatricula() { return dataMatricula; }
    public void setDataMatricula(LocalDate dataMatricula) { this.dataMatricula = dataMatricula; }

    public BigDecimal getNotaFinal() { return notaFinal; }
    public void setNotaFinal(BigDecimal notaFinal) { this.notaFinal = notaFinal; }

    public SituacaoMatricula getSituacao() { return situacao; }
    public void setSituacao(SituacaoMatricula situacao) { this.situacao = situacao; }

}
