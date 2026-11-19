package br.edu.faculdade.carga;

import br.edu.faculdade.academico.model.Curso;
import br.edu.faculdade.academico.model.Disciplina;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Carga de disciplinas a partir de arquivo/curso-disciplina.txt no classpath. */
public final class Carga005Disciplinas {

    private static final String UNIDADE_PERSISTENCIA = "BDJPA";
    private static final String ARQUIVO = "/arquivos/curso-disciplina.txt";

    private record Linha(int numeroLinha, String nomeCurso, String siglaCurso,
                         String siglaDepartamento, int semestre, String codigo,
                         String nome, int cargaHoraria, String tipoDado) {
    }

    private Carga005Disciplinas() {
    }

    public static void main(String[] args) {
        List<Linha> linhas = lerArquivo();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIDADE_PERSISTENCIA);
        try {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                // Carrega cursos e disciplinas previamente existentes em apenas duas consultas.
                List<Curso> cursos = em.createQuery("select c from Curso c join fetch c.departamento", Curso.class)
                    .getResultList();
                Map<String, Curso> cursosPorSigla = new HashMap<>();
                for (Curso curso : cursos) {
                    if (cursosPorSigla.putIfAbsent(curso.getSigla(), curso) != null) {
                        throw new IllegalStateException("Sigla de curso duplicada: " + curso.getSigla());
                    }
                }

                List<Disciplina> disciplinas = em.createQuery("select d from Disciplina d", Disciplina.class)
                    .getResultList();
                Map<String, Disciplina> disciplinasPorCodigo = new HashMap<>();
                for (Disciplina disciplina : disciplinas) {
                    if (disciplinasPorCodigo.putIfAbsent(disciplina.getCodigo(), disciplina) != null) {
                        throw new IllegalStateException("Código de disciplina duplicado: " + disciplina.getCodigo());
                    }
                }

                // Validação integral antes de qualquer alteração no banco.
                Map<String, Curso> cursosValidados = new HashMap<>();
                for (Linha linha : linhas) {
                    Curso curso = cursosPorSigla.get(linha.siglaCurso());
                    if (curso == null) {
                        throw new IllegalStateException("Linha " + linha.numeroLinha()
                            + ": curso não encontrado: " + linha.siglaCurso()
                            + ". Execute antes Carga002Cursos no mesmo banco H2.");
                    }
                    if (!curso.getNome().equals(linha.nomeCurso())) {
                        throw new IllegalStateException("Linha " + linha.numeroLinha()
                            + ": nome do curso não coincide com a sigla " + linha.siglaCurso());
                    }
                    if (!curso.getDepartamento().getSigla().equals(linha.siglaDepartamento())) {
                        throw new IllegalStateException("Linha " + linha.numeroLinha()
                            + ": departamento do curso " + linha.siglaCurso() + " é "
                            + curso.getDepartamento().getSigla() + ", mas o arquivo informa "
                            + linha.siglaDepartamento());
                    }
                    cursosValidados.put(linha.siglaCurso(), curso);
                }

                int inseridas = 0;
                int atualizadas = 0;
                int inalteradas = 0;

                for (Linha linha : linhas) {
                    Curso curso = cursosValidados.get(linha.siglaCurso());
                    Disciplina disciplina = disciplinasPorCodigo.get(linha.codigo());
                    if (disciplina == null) {
                        disciplina = new Disciplina();
                        disciplina.setCodigo(linha.codigo());
                        disciplina.setNome(linha.nome());
                        disciplina.setCargaHoraria(linha.cargaHoraria());
                        disciplina.setSemestre(linha.semestre());
                        disciplina.setTipoDado(linha.tipoDado());
                        disciplina.setCurso(curso);
                        em.persist(disciplina);
                        disciplinasPorCodigo.put(linha.codigo(), disciplina);
                        inseridas++;
                    } else {
                        boolean mudou = false;
                        if (!linha.nome().equals(disciplina.getNome())) {
                            disciplina.setNome(linha.nome());
                            mudou = true;
                        }
                        if (!Integer.valueOf(linha.cargaHoraria()).equals(disciplina.getCargaHoraria())) {
                            disciplina.setCargaHoraria(linha.cargaHoraria());
                            mudou = true;
                        }
                        if (!Integer.valueOf(linha.semestre()).equals(disciplina.getSemestre())) {
                            disciplina.setSemestre(linha.semestre());
                            mudou = true;
                        }
                        if (!linha.tipoDado().equals(disciplina.getTipoDado())) {
                            disciplina.setTipoDado(linha.tipoDado());
                            mudou = true;
                        }
                        UUID cursoAtual = disciplina.getCurso().getId();
                        if (!cursoAtual.equals(curso.getId())) {
                            disciplina.setCurso(curso);
                            mudou = true;
                        }
                        if (mudou) {
                            atualizadas++;
                        } else {
                            inalteradas++;
                        }
                    }
                }

                em.getTransaction().commit();
                System.out.printf("Carga concluída: %d linhas, %d cursos, "
                    + "%d inseridas, %d atualizadas, %d inalteradas.%n",
                    linhas.size(), cursosValidados.size(), inseridas, atualizadas, inalteradas);
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new IllegalStateException("Carga de disciplinas falhou; transação revertida.", e);
            } finally {
                em.close();
            }
        } finally {
            emf.close();
        }
    }

    private static List<Linha> lerArquivo() {
        InputStream arquivo = Carga005Disciplinas.class.getResourceAsStream(ARQUIVO);
        if (arquivo == null) {
            throw new IllegalStateException("Arquivo não encontrado no classpath: " + ARQUIVO);
        }
        List<Linha> linhas = new ArrayList<>();
        Set<String> codigos = new HashSet<>();
        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(arquivo, StandardCharsets.UTF_8))) {
            String texto;
            int linhaAtual = 0;
            while ((texto = leitor.readLine()) != null) {
                linhaAtual++;
                if (texto.isBlank()) {
                    continue;
                }
                String[] campos = texto.split(";", -1);
                if (campos.length != 8) {
                    throw new IllegalArgumentException("Linha " + linhaAtual
                        + ": esperados 8 campos, encontrados " + campos.length);
                }
                for (int i = 0; i < campos.length; i++) {
                    campos[i] = campos[i].trim();
                    if (campos[i].isEmpty()) {
                        throw new IllegalArgumentException("Linha " + linhaAtual
                            + ": campo " + (i + 1) + " vazio");
                    }
                }
                int semestre;
                int cargaHoraria;
                try {
                    semestre = Integer.parseInt(campos[3]);
                    cargaHoraria = Integer.parseInt(campos[6]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Linha " + linhaAtual
                        + ": semestre ou carga horária inválidos", e);
                }
                if (semestre < 1 || semestre > 12 || cargaHoraria <= 0) {
                    throw new IllegalArgumentException("Linha " + linhaAtual
                        + ": semestre deve ser 1 a 12 e carga horária positiva");
                }
                if (campos[4].length() > 30 || campos[5].length() > 150
                        || campos[7].length() > 30) {
                    throw new IllegalArgumentException("Linha " + linhaAtual
                        + ": valor excede o limite da entidade Disciplina");
                }
                if (!codigos.add(campos[4])) {
                    throw new IllegalArgumentException("Linha " + linhaAtual
                        + ": código repetido no arquivo: " + campos[4]);
                }
                linhas.add(new Linha(linhaAtual, campos[0], campos[1], campos[2],
                    semestre, campos[4], campos[5], cargaHoraria, campos[7]));
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Falha ao ler " + ARQUIVO, e);
        }
        if (linhas.isEmpty()) {
            throw new IllegalStateException("Arquivo de disciplinas vazio");
        }
        return linhas;
    }
}
