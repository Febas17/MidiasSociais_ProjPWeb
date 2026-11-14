package br.edu.eseg.midias_sociais.service;

import br.edu.eseg.midias_sociais.model.Conta;
import br.edu.eseg.midias_sociais.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;

    @Transactional(readOnly = true)
    public List<Conta> listarTodos() {
        return contaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Conta> buscarPorId(Integer id) {
        return contaRepository.findById(id);
    }

    @Transactional
    public Conta salvar(Conta conta) {
        return contaRepository.save(conta);
    }

    @Transactional
    public void excluir(Integer id) {
        contaRepository.deleteById(id);
    }
}
