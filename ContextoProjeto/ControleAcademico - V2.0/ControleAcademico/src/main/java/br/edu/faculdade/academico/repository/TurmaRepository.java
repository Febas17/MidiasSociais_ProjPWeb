package br.edu.faculdade.academico.repository;

import br.edu.faculdade.academico.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface TurmaRepository extends JpaRepository<Turma, UUID> {
    List<Turma> findByDisciplinaId(UUID disciplinaId);
    List<Turma> findByProfessorId(UUID professorId);
    List<Turma> findByAnoAndSemestre(Integer ano, Integer semestre);
    long countByAnoAndSemestre(Integer ano, Integer semestre);
}
