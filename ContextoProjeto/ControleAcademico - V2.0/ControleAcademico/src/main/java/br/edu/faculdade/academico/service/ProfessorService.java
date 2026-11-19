package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.repository.ProfessorRepository;
import br.edu.faculdade.academico.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final DepartamentoRepository departamentoRepository;

    public ProfessorService(ProfessorRepository professorRepository, DepartamentoRepository departamentoRepository) {
        this.professorRepository = professorRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Professor> listar() {
        return professorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Professor buscar(UUID id) {
        return professorRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Professor não encontrado: " + id));
    }

    @Transactional
    public Professor cadastrar(Professor dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        if (dados.getId() != null) throw new IllegalArgumentException("ID deve ser gerado pelo banco/JPA");
        if (dados.getDepartamento() == null || dados.getDepartamento().getId() == null) {
            throw new IllegalArgumentException("Departamento é obrigatório");
        }
        dados.setDepartamento(departamentoRepository.findById(dados.getDepartamento().getId())
            .orElseThrow(() -> new NoSuchElementException("Departamento não encontrado")));
        return professorRepository.save(dados);
    }

    @Transactional
    public Professor atualizar(UUID id, Professor dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        Professor atual = buscar(id);
        atual.setRegistro(dados.getRegistro());
        atual.setNome(dados.getNome());
        atual.setEmail(dados.getEmail());
        if (dados.getDepartamento() == null || dados.getDepartamento().getId() == null) {
            throw new IllegalArgumentException("Departamento é obrigatório");
        }
        atual.setDepartamento(departamentoRepository.findById(dados.getDepartamento().getId())
            .orElseThrow(() -> new NoSuchElementException("Departamento não encontrado")));
        return professorRepository.save(atual);
    }

    @Transactional
    public void excluir(UUID id) {
        Professor atual = buscar(id);
        professorRepository.delete(atual);
    }

}
