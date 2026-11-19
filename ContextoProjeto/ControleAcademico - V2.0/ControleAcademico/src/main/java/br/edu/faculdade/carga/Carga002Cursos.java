package br.edu.faculdade.carga;


import br.edu.faculdade.academico.model.Curso;
import br.edu.faculdade.academico.model.Departamento;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class Carga002Cursos {

    private static final String PU_NAME = "BDJPA";

    // Os códigos da carga original correspondem às siglas abaixo.
    private static final Map<Integer, String> SIGLAS_DEPARTAMENTOS = Map.of(
        1, "DELC",
        2, "DGDP",
        3, "DFHPC",
        4, "DCVSH",
        5, "DMEC",
        6, "DEIT"
    );

    record LinhaCurso(String nome, String sigla, int departamentoId) {}

    public static void main(String[] args) {

        final String data = """
            Letras\t1\tLET
            Letras: Formação Tradutor e Intérprete de Libras\t1\tLET-LIB
            Teatro\t1\tTEA
            Música\t1\tMUS
            Artes Visuais\t1\tARTV
            Design Visual\t1\tDESV
            Design de Produto\t1\tDESP
            Dança\t1\tDAN
            História da Arte\t1\tHAR
            Arquitetura e Urbanismo\t1\tARQ
            Ciência da Computação\t5\tCCO
            Administração\t2\tADM
            Astrofísica\t5\tASTRO
            Administração Pública e Social\t2\tADPS
            Ciências Contábeis\t2\tCONT
            Ciências Econômicas\t2\tECO
            Arquivologia\t2\tARQIV
            Biblioteconomia\t2\tBIB
            Direito\t2\tDIR
            Ciências Jurídicas e Sociais: Direito\t2\tCJSD
            Museologia\t2\tMUSL
            Relações Públicas\t2\tRELPU
            Publicidade e Propaganda\t2\tPUB
            Jornalismo\t2\tJOR
            Filosofia\t3\tFIL
            História\t3\tHIS
            Geografia\t3\tGEO
            Pedagogia\t3\tPED
            Educação do Campo, Ciências da Natureza\t3\tEDUCN
            Ciências Sociais\t3\tSOC
            Serviço Social\t3\tSSOC
            Relações Internacionais\t3\tRELINT
            Políticas Públicas\t3\tPOLP
            Ciências Biológicas\t4\tBIO
            Biologia Marinha\t4\tBIOMAR
            Biomedicina\t4\tBIOM
            Nutrição\t4\tNUTE
            Farmácia\t4\tFAR
            Medicina\t4\tMED
            Enfermagem\t4\tENF
            Odontologia\t4\tODO
            Fisioterapia\t4\tFISIO
            Psicologia\t4\tPSI
            Fonoaudiologia\t4\tFONO
            Saúde Coletiva\t4\tSAUC
            Zootecnia\t4\tZOO
            Educação Física\t4\tEDFI
            Agronomia\t4\tAGR
            Medicina Veterinária\t4\tVET
            Biotecnologia\t4\tBIOTEC
            Física\t5\tFIS
            Matemática\t5\tMAT
            Estatística\t5\tEST
            Química\t5\tQUI
            Química Industrial\t5\tQUII
            Geologia\t5\tGEOLOG
            Ciências Atuariais\t5\tATUAR
            Interdisciplinar em Ciência e Tecnologia\t5\tICT
            Engenharia Metalúrgica\t6\tENGMET
            Engenharia Civil\t6\tENGCIV
            Engenharia de Serviços\t6\tENGSERV
            Engenharia Ambiental\t6\tENGAMB
            Engenharia Mecânica\t6\tENGMEC
            Engenharia Elétrica\t6\tENGELE
            Engenharia Química\t6\tENGQUI
            Engenharia Física\t6\tENGFIS
            Engenharia de Computação\t6\tENGCOMP
            Engenharia de Minas\t6\tENGMIN
            Engenharia de Energia\t6\tENGENE
            Engenharia de Controle e Automação\t6\tENGCA
            Engenharia de Materiais\t6\tENGMAT
            Engenharia de Produção\t6\tENGPROD
            Engenharia de Alimentos\t6\tENGALI
            Engenharia de Gestão de Energia\t6\tENGGE
            Engenharia Cartográfica e de Agrimensura\t6\tENGCAR
            Engenharia Hídrica\t6\tENGHID
            """;

        List<LinhaCurso> linhas = parse(data);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME);
        try {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                // Verifica todos os departamentos ANTES de inserir ou alterar cursos.
                Map<Integer, UUID> idsDepartamento = new HashMap<>();
                for (var entry : SIGLAS_DEPARTAMENTOS.entrySet()) {
                    String sigla = entry.getValue();
                    List<Departamento> encontrados = em.createQuery(
                            "select d from Departamento d where d.sigla = :sigla", Departamento.class)
                        .setParameter("sigla", sigla)
                        .getResultList();
                    if (encontrados.size() != 1) {
                        throw new IllegalStateException(
                            "Esperado exatamente um departamento com sigla " + sigla
                            + ", encontrados: " + encontrados.size()
                            + ". Execute antes a carga de departamentos no mesmo banco.");
                    }
                    idsDepartamento.put(entry.getKey(), encontrados.get(0).getId());
                }

                int inseridos = 0;
                int atualizados = 0;
                int inalterados = 0;
                int count = 0;

                for (LinhaCurso linha : linhas) {
                    UUID departamentoId = idsDepartamento.get(linha.departamentoId());
                    if (departamentoId == null) {
                        throw new IllegalArgumentException(
                            "Código de departamento desconhecido: " + linha.departamentoId());
                    }
                    Departamento departamento = em.getReference(Departamento.class, departamentoId);
                    Curso existente = findBySigla(em, linha.sigla());

                    if (existente == null) {
                        Curso novo = new Curso();
                        novo.setNome(linha.nome());
                        novo.setSigla(linha.sigla());
                        novo.setDepartamento(departamento);
                        em.persist(novo);
                        inseridos++;
                    } else {
                        boolean mudou = false;
                        if (!existente.getNome().equals(linha.nome())) {
                            existente.setNome(linha.nome());
                            mudou = true;
                        }
                        if (!existente.getDepartamento().getId().equals(departamentoId)) {
                            existente.setDepartamento(departamento);
                            mudou = true;
                        }
                        if (mudou) {
                            atualizados++;
                        } else {
                            inalterados++;
                        }
                    }

                    if (++count % 50 == 0) {
                        em.flush();
                        em.clear();
                    }
                }

                em.getTransaction().commit();
                System.out.printf(
                    "Carga concluída. Total: %d | Inseridos: %d | Atualizados: %d | Inalterados: %d%n",
                    linhas.size(), inseridos, atualizados, inalterados);

            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Falha na carga automática de cursos. Transação revertida.", e);
            } finally {
                if (em.isOpen()) em.close();
            }
        } finally {
            if (emf.isOpen()) emf.close();
        }
    }

    private static List<LinhaCurso> parse(String data) {
        List<LinhaCurso> out = new ArrayList<>();
        data.lines()
            .map(String::trim)
            .filter(l -> !l.isBlank())
            .forEach(l -> {
                String[] p = l.split("\\t");
                if (p.length < 3) {
                    throw new IllegalArgumentException("Linha inválida: " + l);
                }
                String nome = p[0].trim();
                int depId = Integer.parseInt(p[1].trim());
                String sigla = p[2].trim();
                out.add(new LinhaCurso(nome, sigla, depId));
            });
        return out;
    }

    private static Curso findBySigla(EntityManager em, String sigla) {
        try {
            TypedQuery<Curso> q = em.createQuery(
                "select c from Curso c where c.sigla = :sigla", Curso.class);
            q.setParameter("sigla", sigla);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
