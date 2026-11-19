package br.edu.faculdade.bd.jpa.pkg003;

import br.edu.faculdade.bd.jpa.model.Curso;
import br.edu.faculdade.bd.jpa.model.Discente;
import jakarta.persistence.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Importa Discentes via JPA, a partir de /discentes.txt.
 *
 * Formatos aceitos (delimitador: ';'):
 *   1) MATRICULA;NOME;CURSO_ID
 *   2) MATRICULA;NOME;CURSO_NOME  (fallback: CURSO_SIGLA)
 *
 * Linhas em branco e iniciadas por '#' são ignoradas.
 * Cabeçalho é ignorado se detectar as palavras "matricula" e "nome".
 */
public class ImportarDiscentesJPA {

    private static final String RESOURCE_NAME = "/discentes.txt";
    private static final String PU_NAME = "BDJPA";

    // DTO simples para representar uma linha do arquivo
    record Linha(String matricula, String nome, String cursoToken) {}

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // 1) Carregar linhas do recurso
        List<Linha> linhas = carregarDoResource(RESOURCE_NAME);
        System.out.println("Total lido: " + linhas.size());
        linhas.stream().limit(5).forEach(l ->
            System.out.printf("-> %s | %s | %s%n", l.matricula(), l.nome(), l.cursoToken())
        );

        // 2) Persistir via JPA
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // (defensivo) garantir schema APP ativo caso default_schema não esteja setado
            em.createNativeQuery("CREATE SCHEMA IF NOT EXISTS APP").executeUpdate();
            em.createNativeQuery("SET SCHEMA APP").executeUpdate();

            int count = 0;
            for (Linha l : linhas) {
                upsertDiscente(em, l);

                // Batch hygiene
                if (++count % 50 == 0) {
                    em.flush();
                    em.clear();
                }
            }

            tx.commit();
            System.out.println("Importação de Discentes realizada com sucesso! Total processado: " + linhas.size());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace(System.err);
            System.err.println("Falha na importação de Discentes: " + e.getMessage());
        } finally {
            em.close();
            emf.close();
        }
    }

    /** Lê /discentes.txt com UTF-8 e monta a lista de linhas normalizadas. */
    private static List<Linha> carregarDoResource(String resourcePath) throws Exception {
        InputStream in = ImportarDiscentesJPA.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalStateException("Recurso não encontrado: " + resourcePath + " (coloque em src/main/resources)");
        }

        List<Linha> resultado = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) continue;

                // Ignorar cabeçalho
                String lower = linha.toLowerCase();
                if (lower.contains("matricula") && lower.contains("nome")) continue;

                // Delimitador único: ';' (aceita espaços ao redor do ;)
                String[] cols = linha.split("\\s*;\\s*");
                if (cols.length < 3) {
                    throw new IllegalArgumentException("Linha inválida (esperado 3 colunas separadas por ';'): " + linha);
                }

                String matricula = cols[0].trim();
                String nome = cols[1].trim();
                String cursoToken = cols[2].trim(); // pode ser ID, NOME ou SIGLA

                if (matricula.isEmpty() || nome.isEmpty() || cursoToken.isEmpty()) {
                    throw new IllegalArgumentException("Campos vazios na linha: " + linha);
                }

                resultado.add(new Linha(matricula, nome, cursoToken));
            }
        }
        return resultado;
    }

    /** Upsert por MATRÍCULA; resolve o Curso por ID (se número) ou por NOME/SIGLA. */
    private static void upsertDiscente(EntityManager em, Linha l) {
        Discente existente = findDiscenteByMatricula(em, l.matricula());

        Curso curso = resolveCurso(em, l.cursoToken());
        if (curso == null) {
            throw new IllegalStateException("Curso não encontrado para token: " + l.cursoToken());
        }

        if (existente == null) {
            Discente novo = new Discente();
            novo.setMatricula(l.matricula());
            novo.setNome(l.nome());
            novo.setCurso(curso);
            em.persist(novo);
        } else {
            boolean changed = false;
            if (!l.nome().equals(existente.getNome())) {
                existente.setNome(l.nome());
                changed = true;
            }
            if (existente.getCurso() == null || !existente.getCurso().getId().equals(curso.getId())) {
                existente.setCurso(curso);
                changed = true;
            }
            if (changed) {
                em.merge(existente);
            }
        }
    }

    /** Tenta resolver Curso por ID numérico; se falhar, procura por NOME e depois por SIGLA. */
    private static Curso resolveCurso(EntityManager em, String token) {
        // 1) Tente tratar como ID
        try {
            int id = Integer.parseInt(token);
            Curso ref = em.getReference(Curso.class, id);
            ref.getId(); // valida existência (opcional)
            return ref;
        } catch (NumberFormatException nfe) {
            // não é ID, segue
        } catch (EntityNotFoundException enf) {
            // era numérico mas não existe; cai para busca por nome/sigla
        }

        // 2) Buscar por NOME (match exato)
        Curso byNome = findCursoByNome(em, token);
        if (byNome != null) return byNome;

        // 3) Fallback por SIGLA (ex.: ENGCOMP)
        Curso bySigla = findCursoBySigla(em, token);
        if (bySigla != null) return bySigla;

        return null;
    }

    private static Discente findDiscenteByMatricula(EntityManager em, String matricula) {
        try {
            return em.createQuery(
                    "select d from Discente d where d.matricula = :m", Discente.class)
                .setParameter("m", matricula)
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Curso findCursoByNome(EntityManager em, String nome) {
        try {
            return em.createQuery(
                    "select c from Curso c where c.nome = :n", Curso.class)
                .setParameter("n", nome)
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Curso findCursoBySigla(EntityManager em, String sigla) {
        try {
            return em.createQuery(
                    "select c from Curso c where c.sigla = :s", Curso.class)
                .setParameter("s", sigla)
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
