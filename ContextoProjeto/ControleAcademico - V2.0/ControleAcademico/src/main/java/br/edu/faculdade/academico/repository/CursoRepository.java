package br.edu.faculdade.academico.repository;

import br.edu.faculdade.academico.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, UUID> {
    List<Curso> findByDepartamentoId(UUID departamentoId);
    boolean existsBySiglaIgnoreCase(String sigla);
}
