package br.edu.eseg.midias_sociais.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "APP", name = "PERFIS_SOCIAIS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PERFIL")
    private Integer idPerfil;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONTA", nullable = false, foreignKey = @ForeignKey(name = "FK_PERFIL_CONTA"))
    private Conta conta;

    @Column(name = "PLATAFORMA", nullable = false, length = 50)
    private String plataforma;

    @Column(name = "NOME_PERFIL", nullable = false, length = 100)
    private String nomePerfil;

    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;

    @Column(name = "SEGUIDORES_INICIAIS_2024")
    private Integer seguidoresIniciais2024;

    @Column(name = "SEGUIDORES_ATUAIS")
    private Integer seguidoresAtuais;

    @Column(name = "CATEGORIA_PERFIL", length = 50)
    private String categoriaPerfil;

    @Column(name = "STATUS_PERFIL", length = 20)
    private String statusPerfil;

    // Relacionamentos 1..* com Posts e Audiência Mensal
    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AudienciaMensal> audiencias = new ArrayList<>();
}
