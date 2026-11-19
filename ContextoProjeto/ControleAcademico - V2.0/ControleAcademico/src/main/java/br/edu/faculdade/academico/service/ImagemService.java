package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.repository.ImagemRepository;
import br.edu.faculdade.academico.repository.DiscenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
public class ImagemService {

    private final ImagemRepository imagemRepository;
    private final DiscenteRepository discenteRepository;

    public ImagemService(ImagemRepository imagemRepository, DiscenteRepository discenteRepository) {
        this.imagemRepository = imagemRepository;
        this.discenteRepository = discenteRepository;
    }

    @Transactional(readOnly = true)
    public List<Imagem> listar() {
        return imagemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Imagem buscar(UUID id) {
        return imagemRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Imagem não encontrado: " + id));
    }

    @Transactional
    public Imagem cadastrar(Imagem dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        if (dados.getId() != null) throw new IllegalArgumentException("ID deve ser gerado pelo banco/JPA");
        if (dados.getDiscente() == null || dados.getDiscente().getId() == null) {
            throw new IllegalArgumentException("Discente é obrigatório");
        }
        dados.setDiscente(discenteRepository.findById(dados.getDiscente().getId())
            .orElseThrow(() -> new NoSuchElementException("Discente não encontrado")));
        if (imagemRepository.existsByDiscenteId(dados.getDiscente().getId())) throw new IllegalArgumentException("Discente já possui imagem");
        return imagemRepository.save(dados);
    }

    @Transactional
    public Imagem atualizar(UUID id, Imagem dados) {
        if (dados == null) throw new IllegalArgumentException("Dados obrigatórios");
        Imagem atual = buscar(id);
        atual.setNomeArquivo(dados.getNomeArquivo());
        atual.setTipoMime(dados.getTipoMime());
        atual.setBytes(dados.getBytes());
        if (dados.getDiscente() == null || dados.getDiscente().getId() == null) {
            throw new IllegalArgumentException("Discente é obrigatório");
        }
        atual.setDiscente(discenteRepository.findById(dados.getDiscente().getId())
            .orElseThrow(() -> new NoSuchElementException("Discente não encontrado")));
        if (imagemRepository.findByDiscenteId(atual.getDiscente().getId()).stream().anyMatch(i -> !i.getId().equals(id))) throw new IllegalArgumentException("Discente já possui imagem");
        return imagemRepository.save(atual);
    }

    @Transactional
    public void excluir(UUID id) {
        Imagem atual = buscar(id);
        imagemRepository.delete(atual);
    }

}
