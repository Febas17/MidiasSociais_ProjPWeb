package br.edu.eseg.midias_sociais.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "APP", name = "POSTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_POST")
    private Integer idPost;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PERFIL", nullable = false, foreignKey = @ForeignKey(name = "FK_POST_PERFIL"))
    private PerfilSocial perfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CAMPANHA", foreignKey = @ForeignKey(name = "FK_POST_CAMPANHA"))
    private Campanha campanha;

    @Column(name = "DATA_PUBLICACAO")
    private LocalDate dataPublicacao;

    @Column(name = "TIPO_CONTEUDO", length = 50)
    private String tipoConteudo;

    @Column(name = "TEMA_CONTEUDO", length = 100)
    private String temaConteudo;

    @Column(name = "OBJETIVO", length = 100)
    private String objetivo;

    @Column(name = "CANAL_ORIGEM", length = 50)
    private String canalOrigem;

    @Column(name = "STATUS_POST", length = 30)
    private String statusPost;

    @Column(name = "DURACAO_VIDEO_SEGUNDOS")
    private Integer duracaoVideoSegundos;

    @Column(name = "POSSUI_LINK")
    private Boolean possuiLink;

    @Column(name = "POSSUI_CTA")
    private Boolean possuiCta;

    // Composições 1..* (ciclo de vida dependente do post)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MetricaPost> metricas = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comentario> comentarios = new ArrayList<>();
}
