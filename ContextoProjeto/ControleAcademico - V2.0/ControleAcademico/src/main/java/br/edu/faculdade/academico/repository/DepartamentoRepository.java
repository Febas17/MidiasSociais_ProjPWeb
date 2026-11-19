package br.edu.faculdade.academico.repository;

import br.edu.faculdade.academico.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface DepartamentoRepository extends JpaRepository<Departamento, UUID> {
    List<Departamento> findByNomeContainingIgnoreCase(String nome);
    boolean existsBySiglaIgnoreCase(String sigla);
}
