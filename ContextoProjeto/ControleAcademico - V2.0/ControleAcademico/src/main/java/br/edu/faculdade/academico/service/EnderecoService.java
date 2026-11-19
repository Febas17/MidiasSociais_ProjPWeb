package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional(readOnly = true)
    public List<Endereco> listar() {
        return enderecoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Endereco buscar(UUID id) {
        return enderecoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Endereco não encontrado: " + id));
    }

    @Transactional
    public Endereco cadastrar(Endereco dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        if (dados.getId() != null) throw new IllegalArgumentException("ID deve ser gerado pelo banco/JPA");
        return enderecoRepository.save(dados);
    }

    @Transactional
    public Endereco atualizar(UUID id, Endereco dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        Endereco atual = buscar(id);
        atual.setCep(dados.getCep());
        atual.setLogradouro(dados.getLogradouro());
        atual.setNumero(dados.getNumero());
        atual.setComplemento(dados.getComplemento());
        atual.setBairro(dados.getBairro());
        atual.setCidade(dados.getCidade());
        atual.setUf(dados.getUf());
        return enderecoRepository.save(atual);
    }

    @Transactional
    public void excluir(UUID id) {
        Endereco atual = buscar(id);
        enderecoRepository.delete(atual);
    }

}
