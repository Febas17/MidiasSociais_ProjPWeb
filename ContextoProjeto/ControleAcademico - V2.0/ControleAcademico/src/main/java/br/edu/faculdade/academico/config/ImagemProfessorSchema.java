package br.edu.faculdade.academico.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/** Migração não destrutiva da tabela APP.IMAGEM, executada depois da inicialização JPA. */
@Configuration
public class ImagemProfessorSchema {
    @Bean
    ApplicationRunner ajustarImagemProfessor(JdbcTemplate jdbc) {
        return args -> {
            // Quando a coluna ainda não existir, Hibernate update normalmente a cria.
            // Este comando também atende bancos anteriores à refatoração.
            jdbc.execute("ALTER TABLE APP.IMAGEM ADD COLUMN IF NOT EXISTS PROFESSOR_ID UUID");
            jdbc.execute("ALTER TABLE APP.IMAGEM ALTER COLUMN DISCENTE_ID DROP NOT NULL");
            jdbc.execute("CREATE UNIQUE INDEX IF NOT EXISTS IDX_IMAGEM_PROFESSOR_UNICO " +
                "ON APP.IMAGEM(PROFESSOR_ID)");
            // Se houver imagens antigas de alunos, elas continuam válidas.
            // Impede imagens sem proprietário ou com dois proprietários.
            Integer restricao = jdbc.queryForObject("""
                SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
                WHERE TABLE_SCHEMA='APP' AND TABLE_NAME='IMAGEM'
                  AND CONSTRAINT_NAME='CK_IMAGEM_DONO_EXCLUSIVO'
                """, Integer.class);
            if (restricao != null && restricao == 0) {
                jdbc.execute("""
                    ALTER TABLE APP.IMAGEM ADD CONSTRAINT CK_IMAGEM_DONO_EXCLUSIVO
                    CHECK ((DISCENTE_ID IS NOT NULL AND PROFESSOR_ID IS NULL)
                        OR (DISCENTE_ID IS NULL AND PROFESSOR_ID IS NOT NULL))
                    """);
            }
        };
    }
}
