package br.edu.faculdade.carga;

import br.edu.faculdade.academico.model.Departamento;
import br.edu.faculdade.academico.model.Professor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Carga didática de professores fictícios, sem arquivo externo. */
public class Carga004Professores {

    private static final String PERSISTENCE_UNIT = "BDJPA";

    private record DadosProfessor(String registro, String nome,
                                 String email, String siglaDepartamento) {}

    // Professores e e-mails fictícios, exclusivamente para testes e aulas.
    private static final List<DadosProfessor> DADOS = List.of(
            new DadosProfessor("PROF-0001", "Helena Duarte Almeida", "prof0001@example.org", "DELC"),
            new DadosProfessor("PROF-0002", "Ricardo Vieira Monteiro", "prof0002@example.org", "DELC"),
            new DadosProfessor("PROF-0003", "Clara Martins Barbosa", "prof0003@example.org", "DELC"),
            new DadosProfessor("PROF-0004", "Vinícius Pereira Lemos", "prof0004@example.org", "DELC"),
            new DadosProfessor("PROF-0005", "Laura Mendes Ribeiro", "prof0005@example.org", "DELC"),
            new DadosProfessor("PROF-0006", "Eduardo Farias Costa", "prof0006@example.org", "DELC"),
            new DadosProfessor("PROF-0007", "Mariana Rocha Teixeira", "prof0007@example.org", "DGDP"),
            new DadosProfessor("PROF-0008", "Paulo Henrique Dantas", "prof0008@example.org", "DGDP"),
            new DadosProfessor("PROF-0009", "Fernanda Azevedo Lima", "prof0009@example.org", "DGDP"),
            new DadosProfessor("PROF-0010", "Sérgio Oliveira Prado", "prof0010@example.org", "DGDP"),
            new DadosProfessor("PROF-0011", "Beatriz Nogueira Campos", "prof0011@example.org", "DGDP"),
            new DadosProfessor("PROF-0012", "André Fonseca Reis", "prof0012@example.org", "DGDP"),
            new DadosProfessor("PROF-0013", "Gabriela Lopes Moreira", "prof0013@example.org", "DFHPC"),
            new DadosProfessor("PROF-0014", "Antônio Carlos Nunes", "prof0014@example.org", "DFHPC"),
            new DadosProfessor("PROF-0015", "Camila Sousa Ferraz", "prof0015@example.org", "DFHPC"),
            new DadosProfessor("PROF-0016", "Marcelo Viana Freitas", "prof0016@example.org", "DFHPC"),
            new DadosProfessor("PROF-0017", "Patrícia de Moura Dias", "prof0017@example.org", "DFHPC"),
            new DadosProfessor("PROF-0018", "Rafael Antunes Rocha", "prof0018@example.org", "DFHPC"),
            new DadosProfessor("PROF-0019", "Juliana Ramos Batista", "prof0019@example.org", "DCVSH"),
            new DadosProfessor("PROF-0020", "Fábio Ribeiro Cardoso", "prof0020@example.org", "DCVSH"),
            new DadosProfessor("PROF-0021", "Renata Martins Sales", "prof0021@example.org", "DCVSH"),
            new DadosProfessor("PROF-0022", "Daniel Ferreira Castro", "prof0022@example.org", "DCVSH"),
            new DadosProfessor("PROF-0023", "Isabela Gomes Pereira", "prof0023@example.org", "DCVSH"),
            new DadosProfessor("PROF-0024", "Lucas Almeida Tavares", "prof0024@example.org", "DCVSH"),
            new DadosProfessor("PROF-0025", "Cristina Carvalho Paiva", "prof0025@example.org", "DMEC"),
            new DadosProfessor("PROF-0026", "Rodrigo Barbosa Neri", "prof0026@example.org", "DMEC"),
            new DadosProfessor("PROF-0027", "Aline Fonseca Neves", "prof0027@example.org", "DMEC"),
            new DadosProfessor("PROF-0028", "Tiago Martins Coelho", "prof0028@example.org", "DMEC"),
            new DadosProfessor("PROF-0029", "Vanessa Oliveira Franco", "prof0029@example.org", "DMEC"),
            new DadosProfessor("PROF-0030", "Leandro Pereira Braga", "prof0030@example.org", "DMEC"),
            new DadosProfessor("PROF-0031", "Sofia Lima Figueiredo", "prof0031@example.org", "DEIT"),
            new DadosProfessor("PROF-0032", "Gustavo Ribeiro Pires", "prof0032@example.org", "DEIT"),
            new DadosProfessor("PROF-0033", "Larissa Monteiro Cunha", "prof0033@example.org", "DEIT"),
            new DadosProfessor("PROF-0034", "Bruno Azevedo Martins", "prof0034@example.org", "DEIT"),
            new DadosProfessor("PROF-0035", "Débora Fernandes Lopes", "prof0035@example.org", "DEIT"),
            new DadosProfessor("PROF-0036", "Felipe Santos Andrade", "prof0036@example.org", "DEIT")
    );

    public static void main(String[] args) {
        EntityManagerFactory fabrica = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        try {
            executar(fabrica);
        } finally {
            if (fabrica.isOpen()) {
                fabrica.close();
            }
        }
    }

    private static void executar(EntityManagerFactory fabrica) {
        EntityManager em = fabrica.createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();

            // Primeiro, confirme que todos os departamentos foram cadastrados.
            Map<String, Departamento> departamentos = new HashMap<>();
            for (DadosProfessor dado : DADOS) {
                if (departamentos.containsKey(dado.siglaDepartamento())) {
                    continue;
                }
                List<Departamento> encontrados = em.createQuery(
                        "SELECT d FROM Departamento d WHERE d.sigla = :sigla", Departamento.class)
                        .setParameter("sigla", dado.siglaDepartamento())
                        .getResultList();
                if (encontrados.size() != 1) {
                    throw new IllegalStateException(
                            "Departamento '" + dado.siglaDepartamento() + "' não encontrado "
                            + "ou duplicado. Execute primeiro CargaDepartamentos no mesmo H2.");
                }
                departamentos.put(dado.siglaDepartamento(), encontrados.get(0));
            }

            int inseridos = 0;
            int ignorados = 0;
            Set<String> registrosNoArquivo = new HashSet<>();

            for (DadosProfessor dado : DADOS) {
                if (!registrosNoArquivo.add(dado.registro())) {
                    throw new IllegalStateException("Registro duplicado na carga: " + dado.registro());
                }

                List<Professor> existentes = em.createQuery(
                        "SELECT p FROM Professor p WHERE p.registro = :registro", Professor.class)
                        .setParameter("registro", dado.registro())
                        .getResultList();

                if (!existentes.isEmpty()) {
                    // Reexecutar a classe não cria uma segunda matrícula funcional.
                    ignorados++;
                    continue;
                }

                Professor professor = new Professor();
                professor.setRegistro(dado.registro());
                professor.setNome(dado.nome());
                professor.setEmail(dado.email());
                professor.setDepartamento(departamentos.get(dado.siglaDepartamento()));

                // O ID UUID é gerado pela própria entidade Professor.
                em.persist(professor);
                inseridos++;
            }

            transacao.commit();
            System.out.println("Carga de professores concluída.");
            System.out.println("Inseridos: " + inseridos);
            System.out.println("Já existentes, ignorados: " + ignorados);
            System.out.println("Total de registros previstos: " + DADOS.size());
        } catch (RuntimeException e) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            System.err.println("Carga não concluída. Transação revertida: " + e.getMessage());
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
