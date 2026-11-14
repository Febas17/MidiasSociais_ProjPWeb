package br.edu.eseg.midias_sociais.repository;

import br.edu.eseg.midias_sociais.model.PerfilSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PerfilSocialRepository extends JpaRepository<PerfilSocial, Integer> {
    List<PerfilSocial> findByConta_IdConta(Integer idConta);
}
