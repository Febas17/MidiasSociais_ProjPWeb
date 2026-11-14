package br.edu.eseg.midias_sociais.repository;

import br.edu.eseg.midias_sociais.model.AudienciaMensal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AudienciaMensalRepository extends JpaRepository<AudienciaMensal, Integer> {
    List<AudienciaMensal> findByPerfil_IdPerfil(Integer idPerfil);
}
