package br.edu.faculdade.bd.jpa.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "IMAGEM",
        uniqueConstraints = {
            @UniqueConstraint(name = "UQ_IMAGEM_DISCENTE", columnNames = "DISCENTE_ID")
        },
        indexes = {
            @Index(name = "IDX_IMAGEM_DISCENTE", columnList = "DISCENTE_ID")
        })
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "DISCENTE_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_IMAGEM_DISCENTE"))
    private Discente discente;

    @Column(name = "NOME_ARQUIVO", length = 200)
    private String nomeArquivo;

    @Column(name = "TIPO_MIME", length = 80)
    private String tipoMime;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "BYTES", nullable = false)
    private byte[] bytes;

    @Column(name = "CRIADO_EM", insertable = false, updatable = false)
    private Timestamp criadoEm;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Discente getDiscente() {
        return discente;
    }

    public void setDiscente(Discente discente) {
        this.discente = discente;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Timestamp getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Timestamp criadoEm) {
        this.criadoEm = criadoEm;
    }

}
