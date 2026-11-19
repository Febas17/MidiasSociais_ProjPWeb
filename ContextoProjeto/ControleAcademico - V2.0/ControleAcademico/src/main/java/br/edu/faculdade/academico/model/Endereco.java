package br.edu.faculdade.academico.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "APP", name = "ENDERECO")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "CEP", nullable = false, length = 9)
    private String cep;

    @Column(name = "LOGRADOURO", nullable = false, length = 200)
    private String logradouro;

    @Column(name = "NUMERO", nullable = false, length = 20)
    private String numero;

    @Column(name = "COMPLEMENTO", length = 100)
    private String complemento;

    @Column(name = "BAIRRO", nullable = false, length = 100)
    private String bairro;

    @Column(name = "CIDADE", nullable = false, length = 100)
    private String cidade;

    @Column(name = "UF", nullable = false, length = 2)
    private String uf;

    public Endereco() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
}
