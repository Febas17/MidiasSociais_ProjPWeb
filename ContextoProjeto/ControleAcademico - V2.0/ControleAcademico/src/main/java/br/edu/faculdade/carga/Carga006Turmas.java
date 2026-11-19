package br.edu.faculdade.carga;

import br.edu.faculdade.academico.model.Disciplina;
import br.edu.faculdade.academico.model.Professor;
import br.edu.faculdade.academico.model.Turma;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Cria uma turma por disciplina do primeiro semestre curricular, no período 2026/2. */
public final class Carga006Turmas {

    private static final int ANO_LETIVO = 2026;
    private static final int SEMESTRE_LETIVO = 2;
    private static final int SEMESTRE_CURRICULAR = 1;

    private Carga006Turmas() {}

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

            List<Disciplina> disciplinas = em.createQuery(
                "select d from Disciplina d join fetch d.curso c " +
                "join fetch c.departamento where d.semestre = :semestre " +
                "order by c.sigla, d.codigo", Disciplina.class)
                .setParameter("semestre", SEMESTRE_CURRICULAR)
                .getResultList();
            if (disciplinas.isEmpty()) {
                throw new IllegalStateException("Nenhuma disciplina do semestre curricular 1. Execute a Carga005Disciplinas.");
            }

            List<Professor> professores = em.createQuery(
                "select p from Professor p join fetch p.departamento order by p.registro", Professor.class)
                .getResultList();
            if (professores.isEmpty()) {
                throw new IllegalStateException("Nenhum professor cadastrado. Execute a Carga004Professores.");
            }

            Map<UUID, List<Professor>> porDepartamento = new HashMap<>();
            for (Professor p : professores) {
                UUID dep = p.getDepartamento().getId();
                porDepartamento.computeIfAbsent(dep, k -> new ArrayList<>()).add(p);
            }
            for (List<Professor> lista : porDepartamento.values()) {
                lista.sort(Comparator.comparing(Professor::getRegistro));
            }

            Map<UUID, Integer> rodada = new HashMap<>();
            int inseridas = 0, existentes = 0;
            for (Disciplina disciplina : disciplinas) {
                String codigo = "T-" + disciplina.getCodigo() + "-26-2-A";
                if (codigo.length() > 30) {
                    throw new IllegalArgumentException("Código de turma excede 30 caracteres: " + codigo);
                }
                List<Turma> jaExiste = em.createQuery(
                    "select t from Turma t where t.codigo = :codigo " +
                    "and t.ano = :ano and t.semestre = :semestre", Turma.class)
                    .setParameter("codigo", codigo)
                    .setParameter("ano", ANO_LETIVO)
                    .setParameter("semestre", SEMESTRE_LETIVO)
                    .getResultList();
                if (!jaExiste.isEmpty()) {
                    // Conserva vinculações e professor definidos anteriormente.
                    if (!jaExiste.get(0).getDisciplina().getId().equals(disciplina.getId())) {
                        throw new IllegalStateException("Código de turma vinculado a outra disciplina: " + codigo);
                    }
                    existentes++;
                    continue;
                }

                UUID departamentoId = disciplina.getCurso().getDepartamento().getId();
                List<Professor> candidatos = porDepartamento.get(departamentoId);
                if (candidatos == null || candidatos.isEmpty()) {
                    throw new IllegalStateException("Não há professor no departamento da disciplina: " + disciplina.getCodigo());
                }
                int indice = rodada.getOrDefault(departamentoId, 0);
                Professor professor = candidatos.get(indice % candidatos.size());
                rodada.put(departamentoId, indice + 1);

                Turma turma = new Turma();
                turma.setCodigo(codigo);
                turma.setAno(ANO_LETIVO);
                turma.setSemestre(SEMESTRE_LETIVO);
                turma.setDisciplina(disciplina);
                turma.setProfessor(professor);
                em.persist(turma);
                inseridas++;
            }
            em.getTransaction().commit();
            System.out.printf("Turmas 2026/2: %d inseridas, %d já existentes.%n", inseridas, existentes);
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new IllegalStateException("Carga006Turmas não concluída: rollback aplicado.", e);
        } finally {
            em.close();
        }
    }
}
