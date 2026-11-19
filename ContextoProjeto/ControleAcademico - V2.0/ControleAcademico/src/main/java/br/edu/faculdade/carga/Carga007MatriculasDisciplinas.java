package br.edu.faculdade.carga;

import br.edu.faculdade.academico.model.Discente;
import br.edu.faculdade.academico.model.MatriculaDisciplina;
import br.edu.faculdade.academico.model.SituacaoMatricula;
import br.edu.faculdade.academico.model.Turma;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Matricula cada discente nas turmas do semestre curricular 1 do próprio curso. */
public final class Carga007MatriculasDisciplinas {

    private static final int ANO_LETIVO = 2026;
    private static final int SEMESTRE_LETIVO = 2;
    private static final LocalDate DATA_MATRICULA = LocalDate.of(2026, 8, 1);
    private static final int TAMANHO_LOTE = 250;

    private Carga007MatriculasDisciplinas() {}

    private record Vinculo(UUID discenteId, UUID turmaId) {}

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BDJPA");
        try {
            executar(emf);
        } finally {
            emf.close();
        }
    }

    private static void executar(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            List<Turma> turmas = em.createQuery(
                "select t from Turma t join fetch t.disciplina d " +
                "join fetch d.curso c where t.ano = :ano and t.semestre = :semestre " +
                "and d.semestre = 1 order by c.sigla, d.codigo", Turma.class)
                .setParameter("ano", ANO_LETIVO)
                .setParameter("semestre", SEMESTRE_LETIVO)
                .getResultList();

            Map<UUID, List<UUID>> turmasPorCurso = new HashMap<>();
            for (Turma turma : turmas) {
                if (!turma.getCodigo().startsWith("T-")) continue;
                UUID cursoId = turma.getDisciplina().getCurso().getId();
                turmasPorCurso.computeIfAbsent(cursoId, k -> new ArrayList<>()).add(turma.getId());
            }
            if (turmasPorCurso.isEmpty()) {
                throw new IllegalStateException("Nenhuma turma da carga 006 para 2026/2. Execute Carga006Turmas.");
            }

            List<Discente> discentes = em.createQuery(
                "select d from Discente d join fetch d.curso order by d.matricula", Discente.class)
                .getResultList();
            if (discentes.isEmpty()) {
                throw new IllegalStateException("Nenhum discente cadastrado. Execute a carga de discentes.");
            }

            Set<Vinculo> existentes = new HashSet<>();
            List<Object[]> pares = em.createQuery(
                "select m.discente.id, m.turma.id from MatriculaDisciplina m", Object[].class)
                .getResultList();
            for (Object[] p : pares) {
                existentes.add(new Vinculo((UUID) p[0], (UUID) p[1]));
            }

            int inseridas = 0, ignoradas = 0, semTurmas = 0;
            for (Discente discente : discentes) {
                List<UUID> idsTurmas = turmasPorCurso.get(discente.getCurso().getId());
                if (idsTurmas == null || idsTurmas.isEmpty()) {
                    semTurmas++;
                    continue;
                }
                for (UUID idTurma : idsTurmas) {
                    Vinculo chave = new Vinculo(discente.getId(), idTurma);
                    if (!existentes.add(chave)) {
                        ignoradas++;
                        continue;
                    }
                    MatriculaDisciplina matricula = new MatriculaDisciplina();
                    matricula.setDiscente(em.getReference(Discente.class, discente.getId()));
                    matricula.setTurma(em.getReference(Turma.class, idTurma));
                    matricula.setDataMatricula(DATA_MATRICULA);
                    matricula.setNotaFinal(null);
                    matricula.setSituacao(SituacaoMatricula.MATRICULADO);
                    em.persist(matricula);
                    inseridas++;
                    if (inseridas % TAMANHO_LOTE == 0) {
                        em.flush();
                        em.clear();
                    }
                }
            }
            if (semTurmas > 0) {
                throw new IllegalStateException("Há " + semTurmas +
                    " discentes de cursos sem turmas do 1º semestre. Carga revertida.");
            }
            em.getTransaction().commit();
            System.out.printf("Matrículas 2026/2: %d inseridas; %d já existentes; %d discentes.%n",
                inseridas, ignoradas, discentes.size());
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new IllegalStateException("Carga007MatriculasDisciplinas não concluída: rollback aplicado.", e);
        } finally {
            em.close();
        }
    }
}
