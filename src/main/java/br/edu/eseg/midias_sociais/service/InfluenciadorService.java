package br.edu.eseg.midias_sociais.service;

import br.edu.eseg.midias_sociais.model.Influenciador;
import br.edu.eseg.midias_sociais.repository.InfluenciadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfluenciadorService {

    private final InfluenciadorRepository influenciadorRepository;

    @Transactional(readOnly = true)
    public List<Influenciador> listarTodos() {
        return influenciadorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Influenciador> buscarPorId(Integer id) {
        return influenciadorRepository.findById(id);
    }

    @Transactional
    public Influenciador salvar(Influenciador influenciador) {
        return influenciadorRepository.save(influenciador);
    }

    @Transactional
    public void excluir(Integer id) {
        influenciadorRepository.deleteById(id);
    }
}
