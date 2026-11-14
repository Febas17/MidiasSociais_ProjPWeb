package br.edu.eseg.midias_sociais.repository;

import br.edu.eseg.midias_sociais.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByPerfil_IdPerfil(Integer idPerfil);
    List<Post> findByCampanha_IdCampanha(Integer idCampanha);
    List<Post> findByStatusPost(String statusPost);
}
