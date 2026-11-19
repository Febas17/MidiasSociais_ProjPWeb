package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.repository.DisciplinaRepository;
import br.edu.faculdade.academico.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final CursoRepository cursoRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, CursoRepository cursoRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional(readOnly = true)
    public List<Disciplina> listar() {
        return disciplinaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Disciplina buscar(UUID id) {
        return disciplinaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Disciplina não encontrado: " + id));
    }

    @Transactional
    public Disciplina cadastrar(Disciplina dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        if (dados.getId() != null) throw new IllegalArgumentException("ID deve ser gerado pelo banco/JPA");
        if (dados.getCurso() == null || dados.getCurso().getId() == null) {
            throw new IllegalArgumentException("Curso é obrigatório");
        }
        dados.setCurso(cursoRepository.findById(dados.getCurso().getId())
            .orElseThrow(() -> new NoSuchElementException("Curso não encontrado")));
        return disciplinaRepository.save(dados);
    }

    @Transactional
    public Disciplina atualizar(UUID id, Disciplina dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        Disciplina atual = buscar(id);
        atual.setCodigo(dados.getCodigo());
        atual.setNome(dados.getNome());
        atual.setCargaHoraria(dados.getCargaHoraria());
        if (dados.getCurso() == null || dados.getCurso().getId() == null) {
            throw new IllegalArgumentException("Curso é obrigatório");
        }
        atual.setCurso(cursoRepository.findById(dados.getCurso().getId())
            .orElseThrow(() -> new NoSuchElementException("Curso não encontrado")));
        return disciplinaRepository.save(atual);
    }

    @Transactional
    public void excluir(UUID id) {
        Disciplina atual = buscar(id);
        disciplinaRepository.delete(atual);
    }

}
