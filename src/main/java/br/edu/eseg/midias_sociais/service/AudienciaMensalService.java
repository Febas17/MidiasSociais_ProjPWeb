package br.edu.eseg.midias_sociais.service;

import br.edu.eseg.midias_sociais.model.AudienciaMensal;
import br.edu.eseg.midias_sociais.repository.AudienciaMensalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AudienciaMensalService {

    private final AudienciaMensalRepository audienciaMensalRepository;

    @Transactional(readOnly = true)
    public List<AudienciaMensal> listarTodos() {
        return audienciaMensalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AudienciaMensal> listarPorPerfil(Integer idPerfil) {
        return audienciaMensalRepository.findByPerfil_IdPerfil(idPerfil);
    }

    @Transactional(readOnly = true)
    public Optional<AudienciaMensal> buscarPorId(Integer id) {
        return audienciaMensalRepository.findById(id);
    }

    @Transactional
    public AudienciaMensal salvar(AudienciaMensal audiencia) {
        return audienciaMensalRepository.save(audiencia);
    }

    @Transactional
    public void excluir(Integer id) {
        audienciaMensalRepository.deleteById(id);
    }
}
