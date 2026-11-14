package br.edu.eseg.midias_sociais.repository;

import br.edu.eseg.midias_sociais.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Integer> {
}
