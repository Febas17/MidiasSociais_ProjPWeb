package br.edu.faculdade.academico.repository;

import br.edu.faculdade.academico.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface DisciplinaRepository extends JpaRepository<Disciplina, UUID> {
    List<Disciplina> findByCursoId(UUID cursoId);
    Optional<Disciplina> findByCodigo(String codigo);
}
