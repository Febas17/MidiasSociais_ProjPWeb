package br.edu.eseg.midias_sociais.repository;

import br.edu.eseg.midias_sociais.model.Influenciador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluenciadorRepository extends JpaRepository<Influenciador, Integer> {
}
