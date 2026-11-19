package br.edu.faculdade.carga;

import br.edu.faculdade.academico.model.Curso;
import br.edu.faculdade.academico.model.Discente;
import br.edu.faculdade.academico.model.Endereco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Carga INICIAL dos discentes com endereco obrigatorio, usando o TXT ajustado de 14 colunas. */
public final class Carga003Discentes {

    private static final String UNIDADE_PERSISTENCIA = "BDJPA";
    private static final String RECURSO = "/arquivos/discentes.txt";

    private record Linha(int numero, String matricula, String nome, String curso,
                         String cep, String logradouro, String numeroEndereco,
                         String complemento, String bairro, String cidade, String uf) { }

    private Carga003Discentes() { }

    public static void main(String[] args) throws Exception {
        // Com argumento: caminho do TXT no disco. Sem argumento: carrega em resources/arquivo/.
        List<Linha> linhas = args.length == 0
                ? lerRecurso()
                : lerArquivo(Path.of(args[0]));

        System.out.printf("Arquivo validado: %d discentes.%n", linhas.size());
        EntityManagerFactory fabrica = Persistence.createEntityManagerFactory(UNIDADE_PERSISTENCIA);
        try {
            EntityManager em = fabrica.createEntityManager();
            try {
                EntityTransaction transacao = em.getTransaction();
                try {
                    transacao.begin();

                    // Impede que uma segunda execucao misture uma carga inicial com dados antigos.
                    Long existentes = em.createQuery(
                            "select count(d) from Discente d", Long.class).getSingleResult();
                    if (existentes > 0) {
                        throw new IllegalStateException("Ja existem " + existentes
                                + " discentes no banco. Esta classe executa somente carga inicial.");
                    }

                    // Todos os cursos devem existir ANTES da primeira insercao.
                    // Guarda apenas os UUIDs; em.clear() nao afeta o mapa.
                    Map<String, UUID> cursoPorNome = carregarCursos(em, linhas);

                    int quantidade = 0;
                    for (Linha linha : linhas) {
                        UUID idCurso = cursoPorNome.get(linha.curso());
                        Curso curso = em.getReference(Curso.class, idCurso);

                        Endereco endereco = new Endereco();
                        endereco.setCep(linha.cep());
                        endereco.setLogradouro(linha.logradouro());
                        endereco.setNumero(linha.numeroEndereco());
                        endereco.setComplemento(linha.complemento().isBlank()
                                ? null : linha.complemento());
                        endereco.setBairro(linha.bairro());
                        endereco.setCidade(linha.cidade());
                        endereco.setUf(linha.uf());

                        Discente discente = new Discente();
                        discente.setMatricula(linha.matricula());
                        discente.setNome(linha.nome());
                        discente.setCurso(curso);
                        discente.setEndereco(endereco);

                        // Discente.endereco precisa ter cascade = CascadeType.ALL (ou PERSIST).
                        em.persist(discente);
                        quantidade++;
                        if (quantidade % 100 == 0) {
                            em.flush();
                            em.clear();
                            System.out.printf("Processados: %d/%d%n", quantidade, linhas.size());
                        }
                    }
                    em.flush();
                    transacao.commit();
                    System.out.printf("Carga concluida: %d discentes com enderecos cadastrados.%n",
                            quantidade);
                } catch (Exception erro) {
                    if (transacao.isActive()) transacao.rollback();
                    throw new IllegalStateException(
                            "Carga cancelada e transacao revertida: " + erro.getMessage(), erro);
                }
            } finally {
                if (em.isOpen()) em.close();
            }
        } finally {
            if (fabrica.isOpen()) fabrica.close();
        }
    }

    private static Map<String, UUID> carregarCursos(EntityManager em, List<Linha> linhas) {
        Map<String, List<Curso>> encontrados = new HashMap<>();
        for (Curso curso : em.createQuery("select c from Curso c", Curso.class).getResultList()) {
            encontrados.computeIfAbsent(curso.getNome(), k -> new ArrayList<>()).add(curso);
        }
        Map<String, UUID> mapa = new HashMap<>();
        for (Linha linha : linhas) {
            if (mapa.containsKey(linha.curso())) continue;
            List<Curso> opcoes = encontrados.getOrDefault(linha.curso(), List.of());
            if (opcoes.size() != 1) {
                throw new IllegalStateException("Linha " + linha.numero() + ": curso '"
                        + linha.curso() + "' encontrado " + opcoes.size()
                        + " vez(es) no banco. Execute/verifique a carga de cursos.");
            }
            mapa.put(linha.curso(), opcoes.get(0).getId());
        }
        System.out.printf("Cursos correspondentes no banco: %d%n", mapa.size());
        return mapa;
    }

    private static List<Linha> lerRecurso() throws IOException {
        try (InputStream entrada = Carga003Discentes.class.getResourceAsStream(RECURSO)) {
            if (entrada == null) {
                throw new IllegalStateException("Recurso ausente: " + RECURSO
                        + ". Coloque o TXT em src/main/resources/arquivo/ e recompile o projeto.");
            }
            return analisar(new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8)));
        }
    }

    private static List<Linha> lerArquivo(Path caminho) throws IOException {
        if (!Files.isRegularFile(caminho)) {
            throw new IllegalStateException("Arquivo ausente: " + caminho.toAbsolutePath());
        }
        try (BufferedReader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            return analisar(leitor);
        }
    }

    private static List<Linha> analisar(BufferedReader leitor) throws IOException {
        List<Linha> resultado = new ArrayList<>();
        Set<String> matriculas = new HashSet<>();
        Set<String> enderecos = new HashSet<>();
        String texto;
        int numeroLinha = 0;
        while ((texto = leitor.readLine()) != null) {
            numeroLinha++;
            texto = texto.replace("\uFEFF", "").strip();
            if (texto.isBlank() || texto.startsWith("#")) continue;
            String[] campos = texto.split(";", -1);
            if (campos.length != 14) {
                throw new IllegalArgumentException("Linha " + numeroLinha
                        + ": esperadas 14 colunas; encontradas " + campos.length);
            }
            for (int i = 0; i < campos.length; i++) campos[i] = campos[i].strip();
            if (campos[0].equalsIgnoreCase("MATRICULA")
                    && campos[1].equalsIgnoreCase("NOME")) continue;
            for (int i : new int[]{0, 1, 2, 3, 4, 5, 7, 8, 9}) {
                if (campos[i].isBlank()) {
                    throw new IllegalArgumentException("Linha " + numeroLinha
                            + ": campo obrigatorio vazio na coluna " + (i + 1));
                }
            }
            if (!campos[0].matches("\\d{6}-\\d")) {
                throw new IllegalArgumentException("Linha " + numeroLinha
                        + ": matricula invalida: " + campos[0]);
            }
            if (!campos[3].matches("\\d{5}-\\d{3}")) {
                throw new IllegalArgumentException("Linha " + numeroLinha
                        + ": CEP invalido: " + campos[3]);
            }
            if (!campos[9].matches("[A-Za-z]{2}")) {
                throw new IllegalArgumentException("Linha " + numeroLinha
                        + ": UF invalida: " + campos[9]);
            }
            if (!matriculas.add(campos[0])) {
                throw new IllegalArgumentException("Linha " + numeroLinha
                        + ": matricula duplicada: " + campos[0]);
            }
            String enderecoUnico = String.join("|", campos[3], campos[4], campos[5],
                    campos[6], campos[7], campos[8], campos[9]);
            if (!enderecos.add(enderecoUnico)) {
                throw new IllegalArgumentException("Linha " + numeroLinha
                        + ": endereco repetido: " + enderecoUnico);
            }
            resultado.add(new Linha(numeroLinha, campos[0], campos[1], campos[2],
                    campos[3], campos[4], campos[5], campos[6],
                    campos[7], campos[8], campos[9].toUpperCase()));
        }
        if (resultado.isEmpty()) throw new IllegalStateException("Nenhum discente encontrado.");
        return resultado;
    }
}
