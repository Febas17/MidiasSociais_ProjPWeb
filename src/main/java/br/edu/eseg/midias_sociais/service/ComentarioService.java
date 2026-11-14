package br.edu.eseg.midias_sociais.service;

import br.edu.eseg.midias_sociais.model.Comentario;
import br.edu.eseg.midias_sociais.repository.ComentarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;

    @Transactional(readOnly = true)
    public List<Comentario> listarTodos() {
        return comentarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarPorPost(Integer idPost) {
        return comentarioRepository.findByPost_IdPost(idPost);
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarCriticosParaModeracao() {
        return comentarioRepository.findByRequerAtencaoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Comentario> buscarPorId(Integer id) {
        return comentarioRepository.findById(id);
    }

    @Transactional
    public Comentario salvar(Comentario comentario) {
        // Regra de negócio da entidade periférica adaptada:
        // Se for Spam ou Reclamação com sentimento negativo, ativa flag de atenção
        if (comentario.getCategoriaComentario() != null) {
            String cat = comentario.getCategoriaComentario().toLowerCase();
            String sent = comentario.getSentimento() != null ? comentario.getSentimento().toLowerCase() : "";
            if (cat.contains("spam") || (cat.contains("reclama") && sent.contains("negativo"))) {
                comentario.setRequerAtencao(true);
            }
        }
        return comentarioRepository.save(comentario);
    }

    @Transactional
    public void excluir(Integer id) {
        comentarioRepository.deleteById(id);
    }
}
