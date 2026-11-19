package br.edu.faculdade.carga;


import br.edu.faculdade.academico.model.Departamento;
import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;

/**
 * Carga/seed de Departamentos usando JPA (RESOURCE_LOCAL).
 *
 * Pré-requisitos: - persistence.xml com <persistence-unit name="bdJPA">
 * configurado para H2/Hibernate - Entidade Departamento anotada com @Entity,
 *
 * @Table(schema="APP", name="DEPARTAMENTO") - Uniques por NOME/SIGLA (conforme
 * seu mapeamento)
 */
public class Carga001Departamentos {

    private static final String PU_NAME = "BDJPA";

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME);
        EntityManager em = emf.createEntityManager();

        List<Departamento> departamentos = Arrays.asList(
                new Departamento("Departamento de Expressão, Linguagem e Cultura", "DELC"),
                new Departamento("Departamento de Gestão, Direito e Políticas Públicas", "DGDP"),
                new Departamento("Departamento de Filosofia, História e Pensamento Crítico", "DFHPC"),
                new Departamento("Departamento de Ciências da Vida e Saúde Humana", "DCVSH"),
                new Departamento("Departamento de Matemática, Estatística e Computação", "DMEC"),
                new Departamento("Departamento de Engenharia e Inovação Tecnológica", "DEIT")
        );

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (Departamento departamento : departamentos) {
                upsertPorSigla(em, departamento.getNome(), departamento.getSigla());
            }
            tx.commit();
            System.out.println("Departamentos inseridos/atualizados com sucesso!");
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            System.err.println("Falha ao inserir departamentos: " + e.getMessage());
        } finally {
            em.close();
            emf.close();
        }
    }

    /**
     * Faz “upsert” por SIGLA: - Se já existir departamento com a mesma SIGLA,
     * atualiza o NOME; - Caso contrário, insere um novo.
     */
    private static void upsertPorSigla(EntityManager em, String nome, String sigla) {
        Departamento existente = findBySigla(em, sigla);
        if (existente == null) {
            Departamento novo = new Departamento();
            novo.setNome(nome);
            novo.setSigla(sigla);
            em.persist(novo);
            // em.flush(); // opcional: força ID agora
            System.out.printf("INSERIDO: %s (%s)%n", nome, sigla);
        } else {
            // Atualiza apenas se mudou algo (boa prática)
            boolean mudou = false;
            if (!nome.equals(existente.getNome())) {
                existente.setNome(nome);
                mudou = true;
            }
            if (mudou) {
                em.merge(existente);
                System.out.printf("ATUALIZADO: %s -> %s (%s)%n", existente.getNome(), nome, sigla);
            } else {
                System.out.printf("IGNORADO (sem mudanças): %s (%s)%n", nome, sigla);
            }
        }
    }

    private static Departamento findBySigla(EntityManager em, String sigla) {
        try {
            return em.createQuery(
                    "SELECT d FROM Departamento d WHERE d.sigla = :sigla", Departamento.class)
                    .setParameter("sigla", sigla)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
