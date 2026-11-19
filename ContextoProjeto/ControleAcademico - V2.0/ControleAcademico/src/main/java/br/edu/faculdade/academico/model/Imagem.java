package br.edu.faculdade.academico.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "APP", name = "IMAGEM", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_IMAGEM_DISCENTE", columnNames = "DISCENTE_ID"),
    @UniqueConstraint(name = "UQ_IMAGEM_PROFESSOR", columnNames = "PROFESSOR_ID")
})
public class Imagem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    // Apenas um dos dois vínculos deve estar preenchido.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISCENTE_ID", unique = true,
        foreignKey = @ForeignKey(name = "FK_IMAGEM_DISCENTE"))
    private Discente discente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFESSOR_ID", unique = true,
        foreignKey = @ForeignKey(name = "FK_IMAGEM_PROFESSOR"))
    private Professor professor;

    @Column(name = "NOME_ARQUIVO", length = 200)
    private String nomeArquivo;
    @Column(name = "TIPO_MIME", length = 80)
    private String tipoMime;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "BYTES", nullable = false)
    private byte[] bytes;

    public Imagem() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Discente getDiscente() { return discente; }
    public void setDiscente(Discente discente) { this.discente = discente; }
    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public String getTipoMime() { return tipoMime; }
    public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }
    public byte[] getBytes() { return bytes; }
    public void setBytes(byte[] bytes) { this.bytes = bytes; }
}
