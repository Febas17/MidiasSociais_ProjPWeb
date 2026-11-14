package br.edu.eseg.midias_sociais.service;

import br.edu.eseg.midias_sociais.model.Campanha;
import br.edu.eseg.midias_sociais.repository.CampanhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampanhaService {

    private final CampanhaRepository campanhaRepository;

    @Transactional(readOnly = true)
    public List<Campanha> listarTodos() {
        return campanhaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Campanha> listarPorConta(Integer idConta) {
        return campanhaRepository.findByConta_IdConta(idConta);
    }

    @Transactional(readOnly = true)
    public Optional<Campanha> buscarPorId(Integer id) {
        return campanhaRepository.findById(id);
    }

    @Transactional
    public Campanha salvar(Campanha campanha) {
        // Validação de regra de negócio (data de término não pode ser anterior ao início)
        if (campanha.getDataInicio() != null && campanha.getDataFim() != null) {
            if (campanha.getDataFim().isBefore(campanha.getDataInicio())) {
                throw new IllegalArgumentException("A data de término não pode ser anterior à data de início.");
            }
        }
        return campanhaRepository.save(campanha);
    }

    @Transactional
    public void excluir(Integer id) {
        campanhaRepository.deleteById(id);
    }
}
