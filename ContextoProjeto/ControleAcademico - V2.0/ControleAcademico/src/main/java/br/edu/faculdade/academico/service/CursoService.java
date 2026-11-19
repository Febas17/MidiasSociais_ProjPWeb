package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.repository.CursoRepository;
import br.edu.faculdade.academico.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final DepartamentoRepository departamentoRepository;

    public CursoService(CursoRepository cursoRepository, DepartamentoRepository departamentoRepository) {
        this.cursoRepository = cursoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return cursoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Curso buscar(UUID id) {
        return cursoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Curso não encontrado: " + id));
    }

    @Transactional
    public Curso cadastrar(Curso dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        if (dados.getId() != null) throw new IllegalArgumentException("ID deve ser gerado pelo banco/JPA");
        if (dados.getDepartamento() == null || dados.getDepartamento().getId() == null) {
            throw new IllegalArgumentException("Departamento é obrigatório");
        }
        dados.setDepartamento(departamentoRepository.findById(dados.getDepartamento().getId())
            .orElseThrow(() -> new NoSuchElementException("Departamento não encontrado")));
        return cursoRepository.save(dados);
    }

    @Transactional
    public Curso atualizar(UUID id, Curso dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        Curso atual = buscar(id);
        atual.setNome(dados.getNome());
        atual.setSigla(dados.getSigla());
        if (dados.getDepartamento() == null || dados.getDepartamento().getId() == null) {
            throw new IllegalArgumentException("Departamento é obrigatório");
        }
        atual.setDepartamento(departamentoRepository.findById(dados.getDepartamento().getId())
            .orElseThrow(() -> new NoSuchElementException("Departamento não encontrado")));
        return cursoRepository.save(atual);
    }

    @Transactional
    public void excluir(UUID id) {
        Curso atual = buscar(id);
        cursoRepository.delete(atual);
    }

}
