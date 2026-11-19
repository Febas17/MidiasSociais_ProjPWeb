package br.edu.faculdade.academico.repository;

import br.edu.faculdade.academico.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
    List<Professor> findByDepartamentoId(UUID departamentoId);
    Optional<Professor> findByRegistro(String registro);
}
