package br.edu.faculdade.bd.jpa.pkg002;


import br.edu.faculdade.bd.jpa.util.JPAUtil;
import br.edu.faculdade.bd.jpa.model.Curso;
import br.edu.faculdade.bd.jpa.model.Departamento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class InserirListaCursosJPA {

    // Modelo simples só para parse
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

        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        try {
            int count = 0;

            for (LinhaCurso l : linhas) {
                // 1) Tenta achar por SIGLA (chave natural única do modelo)
                Curso existente = findBySigla(em, l.sigla());

                if (existente == null) {
                    // 2) Não existe: cria novo
                    Curso novo = new Curso();
                    novo.setNome(l.nome());
                    novo.setSigla(l.sigla());
                    // getReference evita SELECT no Departamento, assume PK existente
                    Departamento depRef = em.getReference(Departamento.class, l.departamentoId());
                    novo.setDepartamento(depRef);

                    em.persist(novo);
                } else {
                    // 3) Já existe por SIGLA: opcionalmente, sincronize nome/dep (upsert)
                    boolean changed = false;

                    if (!existente.getNome().equals(l.nome())) {
                        existente.setNome(l.nome());
                        changed = true;
                    }
                    if (!existente.getDepartamento().getId().equals(l.departamentoId())) {
                        Departamento depRef = em.getReference(Departamento.class, l.departamentoId());
                        existente.setDepartamento(depRef);
                        changed = true;
                    }
                    if (changed) {
                        em.merge(existente);
                    }
                }

                // Batch: de tempos em tempos, descarrega no JDBC e libera o 1º nível
                if (++count % 50 == 0) {
                    em.flush();
                    em.clear();
                }
            }

            em.getTransaction().commit();
            System.out.println("Cursos processados com sucesso: " + linhas.size());
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace(System.err);
        } finally {
            if (em.isOpen()) em.close();
            JPAUtil.close();
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
