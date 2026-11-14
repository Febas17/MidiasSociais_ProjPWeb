package br.edu.eseg.midias_sociais.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "APP", name = "INFLUENCIADORES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Influenciador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INFLUENCIADOR")
    private Integer idInfluenciador;

    @Column(name = "NOME_ANONIMIZADO", nullable = false, length = 100)
    private String nomeAnonimizado;

    @Column(name = "NICHO", length = 50)
    private String nicho;

    @Column(name = "PLATAFORMA_PRINCIPAL", length = 50)
    private String plataformaPrincipal;

    @Column(name = "SEGUIDORES")
    private Integer seguidores;

    @Column(name = "TAXA_ENGAJAMENTO_MEDIA")
    private Float taxaEngajamentoMedia;

    @Column(name = "CIDADE", length = 50)
    private String cidade;

    @Column(name = "ESTADO", length = 10)
    private String estado;

    @Column(name = "REGIAO", length = 20)
    private String regiao;

    @Column(name = "STATUS_PARCERIA", length = 30)
    private String statusParceria;

    // Relacionamento 1..* com Parcerias
    @OneToMany(mappedBy = "influenciador")
    @Builder.Default
    private List<Parceria> parcerias = new ArrayList<>();
}
