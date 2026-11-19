package br.edu.faculdade.academico.repository;

import br.edu.faculdade.academico.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {
}
