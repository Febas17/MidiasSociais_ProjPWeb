package br.edu.eseg.midias_sociais.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(schema = "APP", name = "PARCERIAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parceria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PARCERIA")
    private Integer idParceria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INFLUENCIADOR", nullable = false, foreignKey = @ForeignKey(name = "FK_PARCERIA_INFLUENCIADOR"))
    private Influenciador influenciador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONTA", nullable = false, foreignKey = @ForeignKey(name = "FK_PARCERIA_CONTA"))
    private Conta conta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CAMPANHA", foreignKey = @ForeignKey(name = "FK_PARCERIA_CAMPANHA"))
    private Campanha campanha;

    @Column(name = "DATA_PARCERIA")
    private LocalDate dataParceria;

    @Column(name = "PLATAFORMA", length = 50)
    private String plataforma;

    @Column(name = "TIPO_ENTREGA", length = 50)
    private String tipoEntrega;

    @Column(name = "CUSTO_PARCERIA")
    private Float custoParceria;

    @Column(name = "ALCANCE_ESTIMADO")
    private Integer alcanceEstimado;

    @Column(name = "ENGAJAMENTOS_ESTIMADOS")
    private Integer engajamentosEstimados;

    @Column(name = "VENDAS_ATRIBUIDAS")
    private Integer vendasAtribuidas;

    @Column(name = "RECEITA_ATRIBUIDA")
    private Float receitaAtribuida;

    @Column(name = "STATUS_PARCERIA", length = 30)
    private String statusParceria;
}
