package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Departamento> listar() {
        return departamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Departamento buscar(UUID id) {
        return departamentoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Departamento não encontrado: " + id));
    }

    @Transactional
    public Departamento cadastrar(Departamento dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        if (dados.getId() != null) throw new IllegalArgumentException("ID deve ser gerado pelo banco/JPA");
        return departamentoRepository.save(dados);
    }

    @Transactional
    public Departamento atualizar(UUID id, Departamento dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        Departamento atual = buscar(id);
        atual.setNome(dados.getNome());
        atual.setSigla(dados.getSigla());
        return departamentoRepository.save(atual);
    }

    @Transactional
    public void excluir(UUID id) {
        Departamento atual = buscar(id);
        departamentoRepository.delete(atual);
    }

}
