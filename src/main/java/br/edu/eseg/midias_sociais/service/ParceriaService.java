package br.edu.eseg.midias_sociais.service;

import br.edu.eseg.midias_sociais.model.Parceria;
import br.edu.eseg.midias_sociais.repository.ParceriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParceriaService {

    private final ParceriaRepository parceriaRepository;

    @Transactional(readOnly = true)
    public List<Parceria> listarTodos() {
        return parceriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Parceria> listarPorInfluenciador(Integer idInfluenciador) {
        return parceriaRepository.findByInfluenciador_IdInfluenciador(idInfluenciador);
    }

    @Transactional(readOnly = true)
    public List<Parceria> listarPorConta(Integer idConta) {
        return parceriaRepository.findByConta_IdConta(idConta);
    }

    @Transactional(readOnly = true)
    public Optional<Parceria> buscarPorId(Integer id) {
        return parceriaRepository.findById(id);
    }

    @Transactional
    public Parceria salvar(Parceria parceria) {
        return parceriaRepository.save(parceria);
    }

    @Transactional
    public void excluir(Integer id) {
        parceriaRepository.deleteById(id);
    }
}
