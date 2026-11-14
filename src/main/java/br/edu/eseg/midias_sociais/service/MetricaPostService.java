package br.edu.eseg.midias_sociais.service;

import br.edu.eseg.midias_sociais.model.MetricaPost;
import br.edu.eseg.midias_sociais.repository.MetricaPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MetricaPostService {

    private final MetricaPostRepository metricaPostRepository;

    @Transactional(readOnly = true)
    public List<MetricaPost> listarTodos() {
        return metricaPostRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<MetricaPost> listarPorPost(Integer idPost) {
        return metricaPostRepository.findByPost_IdPost(idPost);
    }

    @Transactional(readOnly = true)
    public Optional<MetricaPost> buscarPorId(Integer id) {
        return metricaPostRepository.findById(id);
    }

    @Transactional
    public MetricaPost salvar(MetricaPost metrica) {
        return metricaPostRepository.save(metrica);
    }

    @Transactional
    public void excluir(Integer id) {
        metricaPostRepository.deleteById(id);
    }
}
