package br.edu.eseg.midias_sociais.service;

import br.edu.eseg.midias_sociais.model.Post;
import br.edu.eseg.midias_sociais.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<Post> listarTodos() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Post> listarPorPerfil(Integer idPerfil) {
        return postRepository.findByPerfil_IdPerfil(idPerfil);
    }

    @Transactional(readOnly = true)
    public List<Post> listarPorCampanha(Integer idCampanha) {
        return postRepository.findByCampanha_IdCampanha(idCampanha);
    }

    @Transactional(readOnly = true)
    public Optional<Post> buscarPorId(Integer id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post salvar(Post post) {
        return postRepository.save(post);
    }

    @Transactional
    public void excluir(Integer id) {
        postRepository.deleteById(id);
    }
}
