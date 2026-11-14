package br.edu.eseg.midias_sociais.repository;

import br.edu.eseg.midias_sociais.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {
    List<Comentario> findByPost_IdPost(Integer idPost);
    List<Comentario> findByRequerAtencaoTrue();
    List<Comentario> findByStatusModeracao(String statusModeracao);
}
