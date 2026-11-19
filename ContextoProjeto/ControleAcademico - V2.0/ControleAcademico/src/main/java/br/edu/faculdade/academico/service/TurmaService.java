package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.repository.TurmaRepository;
import br.edu.faculdade.academico.repository.DisciplinaRepository;
import br.edu.faculdade.academico.repository.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final ProfessorRepository professorRepository;

    public TurmaService(TurmaRepository turmaRepository, DisciplinaRepository disciplinaRepository, ProfessorRepository professorRepository) {
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional(readOnly = true)
    public List<Turma> listar() {
        return turmaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Turma buscar(UUID id) {
        return turmaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Turma não encontrado: " + id));
    }

    @Transactional
    public Turma cadastrar(Turma dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        if (dados.getId() != null) throw new IllegalArgumentException("ID deve ser gerado pelo banco/JPA");
        if (dados.getDisciplina() == null || dados.getDisciplina().getId() == null) {
            throw new IllegalArgumentException("Disciplina é obrigatório");
        }
        dados.setDisciplina(disciplinaRepository.findById(dados.getDisciplina().getId())
            .orElseThrow(() -> new NoSuchElementException("Disciplina não encontrado")));
        if (dados.getProfessor() == null || dados.getProfessor().getId() == null) {
            throw new IllegalArgumentException("Professor é obrigatório");
        }
        dados.setProfessor(professorRepository.findById(dados.getProfessor().getId())
            .orElseThrow(() -> new NoSuchElementException("Professor não encontrado")));
        return turmaRepository.save(dados);
    }

    @Transactional
    public Turma atualizar(UUID id, Turma dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        Turma atual = buscar(id);
        atual.setCodigo(dados.getCodigo());
        atual.setAno(dados.getAno());
        atual.setSemestre(dados.getSemestre());
        if (dados.getDisciplina() == null || dados.getDisciplina().getId() == null) {
            throw new IllegalArgumentException("Disciplina é obrigatório");
        }
        atual.setDisciplina(disciplinaRepository.findById(dados.getDisciplina().getId())
            .orElseThrow(() -> new NoSuchElementException("Disciplina não encontrado")));
        if (dados.getProfessor() == null || dados.getProfessor().getId() == null) {
            throw new IllegalArgumentException("Professor é obrigatório");
        }
        atual.setProfessor(professorRepository.findById(dados.getProfessor().getId())
            .orElseThrow(() -> new NoSuchElementException("Professor não encontrado")));
        return turmaRepository.save(atual);
    }

    @Transactional
    public void excluir(UUID id) {
        Turma atual = buscar(id);
        turmaRepository.delete(atual);
    }

}
