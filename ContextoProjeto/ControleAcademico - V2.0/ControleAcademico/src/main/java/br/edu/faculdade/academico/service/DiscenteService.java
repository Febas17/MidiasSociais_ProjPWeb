package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.Curso;
import br.edu.faculdade.academico.model.Discente;
import br.edu.faculdade.academico.model.Endereco;
import br.edu.faculdade.academico.repository.CursoRepository;
import br.edu.faculdade.academico.repository.DiscenteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DiscenteService {

    private final DiscenteRepository discenteRepository;
    private final CursoRepository cursoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public DiscenteService(DiscenteRepository discenteRepository,
                          CursoRepository cursoRepository) {
        this.discenteRepository = discenteRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional(readOnly = true)
    public List<Discente> listar() {
        return discenteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Discente buscar(UUID id) {
        return discenteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Discente não encontrado: " + id));
    }

    @Transactional
    public Discente cadastrar(Discente dados) {
        validarDadosSemMatricula(dados);
        if (dados.getId() != null) {
            throw new IllegalArgumentException("O UUID deve ser gerado pelo JPA.");
        }

        Curso curso = carregarCurso(dados.getCurso().getId());
        Endereco endereco = dados.getEndereco();
        validarEndereco(endereco);

        // A matrícula do formulário é desconsiderada na criação.
        Number numero = (Number) entityManager.createNativeQuery(
                "SELECT NEXT VALUE FOR APP.SEQ_MATRICULA").getSingleResult();
        dados.setMatricula(GeradorMatricula.gerar(numero.longValue()));
        dados.setCurso(curso);
        endereco.setId(null);
        return discenteRepository.save(dados);
    }

    @Transactional
    public Discente atualizar(UUID id, Discente dados) {
        if (id == null) {
            throw new IllegalArgumentException("Identificador do discente é obrigatório.");
        }
        // Primeiro recupera o registro gerenciado e a matrícula original.
        Discente existente = discenteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Discente não encontrado: " + id));

        // A matrícula NÃO vem do formulário e não deve ser alterada.
        // Não chamar validarDados(dados), pois dados.getMatricula() pode ser null.
        validarDadosSemMatricula(dados);
        validarMatricula(existente.getMatricula());
        validarEndereco(dados.getEndereco());

        Curso curso = carregarCurso(dados.getCurso().getId());
        existente.setNome(dados.getNome().trim());
        existente.setCurso(curso);

        Endereco origem = dados.getEndereco();
        Endereco destino = existente.getEndereco();
        if (destino == null) {
            // Compatibilidade com registros antigos sem endereço.
            origem.setId(null);
            existente.setEndereco(origem);
        } else {
            // Atualizar a instância gerenciada preserva seu UUID e o vínculo 1:1.
            copiarEndereco(origem, destino);
        }

        // Sem setMatricula(): preserva a matrícula do banco.
        return discenteRepository.save(existente);
    }

    @Transactional
    public void excluir(UUID id) {
        Discente existente = discenteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Discente não encontrado: " + id));
        discenteRepository.delete(existente);
        // A exclusão da fotografia deve permanecer no Controller/DiscenteFotoService.
    }

    private Curso carregarCurso(UUID cursoId) {
        return cursoRepository.findById(cursoId)
                .orElseThrow(() -> new NoSuchElementException("Curso não encontrado: " + cursoId));
    }

    private static void validarDadosSemMatricula(Discente dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados do discente são obrigatórios.");
        }
        if (dados.getNome() == null || dados.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (dados.getCurso() == null || dados.getCurso().getId() == null) {
            throw new IllegalArgumentException("Curso é obrigatório.");
        }
        if (dados.getEndereco() == null) {
            throw new IllegalArgumentException("Endereço é obrigatório.");
        }
    }

    private static void validarMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalStateException("A matrícula do discente cadastrado está ausente no banco.");
        }
    }

    private static void validarEndereco(Endereco endereco) {
        if (endereco == null
                || endereco.getCep() == null
                || !endereco.getCep().trim().matches("\\d{5}-?\\d{3}")
                || endereco.getLogradouro() == null || endereco.getLogradouro().isBlank()
                || endereco.getNumero() == null || endereco.getNumero().isBlank()
                || endereco.getBairro() == null || endereco.getBairro().isBlank()
                || endereco.getCidade() == null || endereco.getCidade().isBlank()
                || endereco.getUf() == null
                || !endereco.getUf().trim().matches("(?i)[A-Z]{2}")) {
            throw new IllegalArgumentException(
                    "Endereço completo é obrigatório; complemento é opcional.");
        }
    }

    private static void copiarEndereco(Endereco origem, Endereco destino) {
        destino.setCep(origem.getCep().trim());
        destino.setLogradouro(origem.getLogradouro().trim());
        destino.setNumero(origem.getNumero().trim());
        destino.setComplemento(origem.getComplemento() == null ? "" : origem.getComplemento().trim());
        destino.setBairro(origem.getBairro().trim());
        destino.setCidade(origem.getCidade().trim());
        destino.setUf(origem.getUf().trim().toUpperCase(Locale.ROOT));
    }
}
