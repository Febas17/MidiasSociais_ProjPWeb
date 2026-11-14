package br.edu.eseg.midias_sociais.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(schema = "APP", name = "COMENTARIOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMENTARIO")
    private Integer idComentario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_POST", nullable = false, foreignKey = @ForeignKey(name = "FK_COMENTARIO_POST"))
    private Post post;

    @Column(name = "DATA_COMENTARIO")
    private LocalDate dataComentario;

    // Adaptação da entidade periférica (texto real da mensagem)
    @Column(name = "TEXTO_COMENTARIO", length = 500)
    private String textoComentario;

    @Column(name = "SENTIMENTO", length = 30)
    private String sentimento;

    @Column(name = "CATEGORIA_COMENTARIO", length = 50)
    private String categoriaComentario;

    @Column(name = "TEMPO_RESPOSTA_HORAS")
    private Float tempoRespostaHoras;

    // Adaptação da entidade periférica (flag de triagem/atenção imediata)
    @Column(name = "REQUER_ATENCAO")
    private Boolean requerAtencao;

    @Column(name = "STATUS_MODERACAO", length = 30)
    private String statusModeracao;
}
