package br.edu.faculdade.academico.repository;

import br.edu.faculdade.academico.model.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface ImagemRepository extends JpaRepository<Imagem, UUID> {
    Optional<Imagem> findByDiscenteId(UUID discenteId);
    boolean existsByDiscenteId(UUID discenteId);
}
