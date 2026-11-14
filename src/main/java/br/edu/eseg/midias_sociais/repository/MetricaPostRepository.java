package br.edu.eseg.midias_sociais.repository;

import br.edu.eseg.midias_sociais.model.MetricaPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MetricaPostRepository extends JpaRepository<MetricaPost, Integer> {
    List<MetricaPost> findByPost_IdPost(Integer idPost);
}
