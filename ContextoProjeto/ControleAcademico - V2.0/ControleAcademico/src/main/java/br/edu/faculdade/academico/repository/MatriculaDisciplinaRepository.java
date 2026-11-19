package br.edu.faculdade.academico.repository;

import br.edu.faculdade.academico.model.MatriculaDisciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface MatriculaDisciplinaRepository extends JpaRepository<MatriculaDisciplina, UUID> {
    List<MatriculaDisciplina> findByDiscenteId(UUID discenteId);
    List<MatriculaDisciplina> findByTurmaId(UUID turmaId);
    boolean existsByDiscenteIdAndTurmaId(UUID discenteId, UUID turmaId);
    long countByTurma_AnoAndTurma_Semestre(Integer ano, Integer semestre);
}
