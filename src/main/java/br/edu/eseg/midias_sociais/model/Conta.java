package br.edu.eseg.midias_sociais.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "APP", name = "CONTAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONTA")
    private Integer idConta;

    @Column(name = "NOME_CONTA", nullable = false, length = 100)
    private String nomeConta;

    @Column(name = "SEGMENTO", length = 50)
    private String segmento;

    @Column(name = "CIDADE", length = 50)
    private String cidade;

    @Column(name = "ESTADO", length = 10)
    private String estado;

    @Column(name = "REGIAO", length = 20)
    private String regiao;

    @Column(name = "PORTE_EMPRESA", length = 30)
    private String porteEmpresa;

    @Column(name = "ANO_INICIO_OPERACAO")
    private Integer anoInicioOperacao;

    @Column(name = "STATUS_CONTA", length = 20)
    private String statusConta;

    // Relacionamentos 1..* com Perfis e Campanhas
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PerfilSocial> perfis = new ArrayList<>();

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Campanha> campanhas = new ArrayList<>();
}
