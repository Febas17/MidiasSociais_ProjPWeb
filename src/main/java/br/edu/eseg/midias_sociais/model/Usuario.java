package br.edu.eseg.midias_sociais.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "APP", name = "USUARIOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "SENHA", nullable = false, length = 100)
    private String senha;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERFIL", nullable = false, length = 20)
    private PerfilAcesso perfil;

    @Column(name = "ATIVO")
    @Builder.Default
    private Boolean ativo = true;
}
