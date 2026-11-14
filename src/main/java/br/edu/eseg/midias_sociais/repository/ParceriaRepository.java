package br.edu.eseg.midias_sociais.repository;

import br.edu.eseg.midias_sociais.model.Parceria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParceriaRepository extends JpaRepository<Parceria, Integer> {
    List<Parceria> findByInfluenciador_IdInfluenciador(Integer idInfluenciador);
    List<Parceria> findByConta_IdConta(Integer idConta);
    List<Parceria> findByCampanha_IdCampanha(Integer idCampanha);
}
