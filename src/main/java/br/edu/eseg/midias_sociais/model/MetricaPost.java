package br.edu.eseg.midias_sociais.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(schema = "APP", name = "METRICAS_POSTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricaPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_METRICA")
    private Integer idMetrica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_POST", nullable = false, foreignKey = @ForeignKey(name = "FK_METRICA_POST"))
    private Post post;

    @Column(name = "DATA_REFERENCIA")
    private LocalDate dataReferencia;

    @Column(name = "CANAL_ORIGEM", length = 50)
    private String canalOrigem;

    @Column(name = "IMPRESSOES")
    private Integer impressoes;

    @Column(name = "ALCANCE")
    private Integer alcance;

    @Column(name = "CURTIDAS")
    private Integer curtidas;

    @Column(name = "COMENTARIOS")
    private Integer comentarios;

    @Column(name = "COMPARTILHAMENTOS")
    private Integer compartilhamentos;

    @Column(name = "SALVAMENTOS")
    private Integer salvamentos;

    @Column(name = "CLIQUES_LINK")
    private Integer cliquesLink;

    @Column(name = "VISUALIZACOES_VIDEO")
    private Integer visualizacoesVideo;

    @Column(name = "NOVOS_SEGUIDORES")
    private Integer novosSeguidores;

    @Column(name = "CUSTO_IMPULSIONAMENTO")
    private Float custoImpulsionamento;

    @Column(name = "RECEITA_ATRIBUIDA")
    private Float receitaAtribuida;

    @Column(name = "SENTIMENTO_PREDOMINANTE", length = 30)
    private String sentimentoPredominante;

    // Métodos de cálculo sob demanda (conforme justificado na Matriz)
    public Double getTaxaEngajamento() {
        if (alcance == null || alcance == 0) return 0.0;
        int engajamentos = (curtidas != null ? curtidas : 0)
                + (comentarios != null ? comentarios : 0)
                + (compartilhamentos != null ? compartilhamentos : 0)
                + (salvamentos != null ? salvamentos : 0);
        return (double) engajamentos / alcance;
    }

    public Double getCtr() {
        if (impressoes == null || impressoes == 0) return 0.0;
        int cliques = (cliquesLink != null ? cliquesLink : 0);
        return (double) cliques / impressoes;
    }
}
