package br.edu.eseg.midias_sociais.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "APP", name = "CAMPANHAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campanha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CAMPANHA")
    private Integer idCampanha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONTA", nullable = false, foreignKey = @ForeignKey(name = "FK_CAMPANHA_CONTA"))
    private Conta conta;

    @Column(name = "NOME_CAMPANHA", nullable = false, length = 150)
    private String nomeCampanha;

    @Column(name = "OBJETIVO", length = 100)
    private String objetivo;

    @Column(name = "DATA_INICIO")
    private LocalDate dataInicio;

    @Column(name = "DATA_FIM")
    private LocalDate dataFim;

    @Column(name = "ORCAMENTO_PLANEJADO")
    private Float orcamentoPlanejado;

    @Column(name = "CANAL_ORIGEM", length = 50)
    private String canalOrigem;

    @Column(name = "STATUS_CAMPANHA", length = 30)
    private String statusCampanha;

    // Relacionamentos 1..* com Posts e Parcerias
    @OneToMany(mappedBy = "campanha")
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "campanha")
    @Builder.Default
    private List<Parceria> parcerias = new ArrayList<>();
}
