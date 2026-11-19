package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.repository.MatriculaDisciplinaRepository;
import br.edu.faculdade.academico.repository.DiscenteRepository;
import br.edu.faculdade.academico.repository.TurmaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;
import java.time.LocalDate;
import java.math.BigDecimal;

@Service
public class MatriculaDisciplinaService {

    private final MatriculaDisciplinaRepository matriculaDisciplinaRepository;
    private final DiscenteRepository discenteRepository;
    private final TurmaRepository turmaRepository;

    public MatriculaDisciplinaService(MatriculaDisciplinaRepository matriculaDisciplinaRepository, DiscenteRepository discenteRepository, TurmaRepository turmaRepository) {
        this.matriculaDisciplinaRepository = matriculaDisciplinaRepository;
        this.discenteRepository = discenteRepository;
        this.turmaRepository = turmaRepository;
    }

    @Transactional(readOnly = true)
    public List<MatriculaDisciplina> listar() {
        return matriculaDisciplinaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MatriculaDisciplina buscar(UUID id) {
        return matriculaDisciplinaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("MatriculaDisciplina não encontrado: " + id));
    }

    @Transactional
    public MatriculaDisciplina cadastrar(MatriculaDisciplina dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        if (dados.getId() != null) throw new IllegalArgumentException("ID deve ser gerado pelo banco/JPA");
        if (dados.getDiscente() == null || dados.getDiscente().getId() == null) {
            throw new IllegalArgumentException("Discente é obrigatório");
        }
        dados.setDiscente(discenteRepository.findById(dados.getDiscente().getId())
            .orElseThrow(() -> new NoSuchElementException("Discente não encontrado")));
        if (dados.getTurma() == null || dados.getTurma().getId() == null) {
            throw new IllegalArgumentException("Turma é obrigatório");
        }
        dados.setTurma(turmaRepository.findById(dados.getTurma().getId())
            .orElseThrow(() -> new NoSuchElementException("Turma não encontrado")));
        if (matriculaDisciplinaRepository.existsByDiscenteIdAndTurmaId(dados.getDiscente().getId(), dados.getTurma().getId())) {
            throw new IllegalArgumentException("Discente já matriculado nesta turma");
        }
        if (dados.getDataMatricula() == null) dados.setDataMatricula(LocalDate.now());
        if (dados.getSituacao() == null) dados.setSituacao(SituacaoMatricula.MATRICULADO);
        validarNota(dados.getNotaFinal());
        return matriculaDisciplinaRepository.save(dados);
    }

    @Transactional
    public MatriculaDisciplina atualizar(UUID id, MatriculaDisciplina dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        MatriculaDisciplina atual = buscar(id);
        atual.setNotaFinal(dados.getNotaFinal());
        atual.setSituacao(dados.getSituacao());
        if (dados.getDiscente() == null || dados.getDiscente().getId() == null) {
            throw new IllegalArgumentException("Discente é obrigatório");
        }
        atual.setDiscente(discenteRepository.findById(dados.getDiscente().getId())
            .orElseThrow(() -> new NoSuchElementException("Discente não encontrado")));
        if (dados.getTurma() == null || dados.getTurma().getId() == null) {
            throw new IllegalArgumentException("Turma é obrigatório");
        }
        atual.setTurma(turmaRepository.findById(dados.getTurma().getId())
            .orElseThrow(() -> new NoSuchElementException("Turma não encontrado")));
        if (matriculaDisciplinaRepository.existsByDiscenteIdAndTurmaId(atual.getDiscente().getId(), atual.getTurma().getId())) {
            // Validar duplicidade ignorando a própria matrícula
            boolean outra = matriculaDisciplinaRepository.findByDiscenteId(atual.getDiscente().getId()).stream()
                .anyMatch(m -> !m.getId().equals(id) && m.getTurma().getId().equals(atual.getTurma().getId()));
            if (outra) throw new IllegalArgumentException("Discente já matriculado nesta turma");
        }
        validarNota(atual.getNotaFinal());
        return matriculaDisciplinaRepository.save(atual);
    }

    @Transactional
    public void excluir(UUID id) {
        MatriculaDisciplina atual = buscar(id);
        matriculaDisciplinaRepository.delete(atual);
    }

    private void validarNota(BigDecimal nota) {
        if (nota != null && (nota.compareTo(BigDecimal.ZERO) < 0
                || nota.compareTo(new BigDecimal("10.00")) > 0)) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
    }

}
