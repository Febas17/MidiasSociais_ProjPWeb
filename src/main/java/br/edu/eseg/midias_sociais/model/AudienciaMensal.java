package br.edu.eseg.midias_sociais.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "APP", name = "AUDIENCIA_MENSAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AudienciaMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AUDIENCIA")
    private Integer idAudiencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PERFIL", nullable = false, foreignKey = @ForeignKey(name = "FK_AUDIENCIA_PERFIL"))
    private PerfilSocial perfil;

    @Column(name = "ANO", nullable = false)
    private Integer ano;

    @Column(name = "MES", nullable = false)
    private Integer mes;

    @Column(name = "FAIXA_ETARIA", length = 30)
    private String faixaEtaria;

    @Column(name = "GENERO", length = 30)
    private String genero;

    @Column(name = "CIDADE", length = 50)
    private String cidade;

    @Column(name = "ESTADO", length = 10)
    private String estado;

    @Column(name = "REGIAO", length = 20)
    private String regiao;

    @Column(name = "SEGUIDORES")
    private Integer seguidores;

    @Column(name = "PERCENTUAL_AUDIENCIA")
    private Float percentualAudiencia;
}
