package br.edu.faculdade.academico.repository;

import br.edu.faculdade.academico.model.Discente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface DiscenteRepository extends JpaRepository<Discente, UUID> {
    List<Discente> findByCursoId(UUID cursoId);
    Optional<Discente> findByMatricula(String matricula);
}
