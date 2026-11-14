package br.edu.eseg.midias_sociais.repository;

import br.edu.eseg.midias_sociais.model.Campanha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampanhaRepository extends JpaRepository<Campanha, Integer> {
    List<Campanha> findByConta_IdConta(Integer idConta);
    List<Campanha> findByStatusCampanha(String status);
}
