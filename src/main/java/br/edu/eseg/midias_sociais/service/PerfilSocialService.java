package br.edu.eseg.midias_sociais.service;

import br.edu.eseg.midias_sociais.model.PerfilSocial;
import br.edu.eseg.midias_sociais.repository.PerfilSocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerfilSocialService {

    private final PerfilSocialRepository perfilSocialRepository;

    @Transactional(readOnly = true)
    public List<PerfilSocial> listarTodos() {
        return perfilSocialRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PerfilSocial> listarPorConta(Integer idConta) {
        return perfilSocialRepository.findByConta_IdConta(idConta);
    }

    @Transactional(readOnly = true)
    public Optional<PerfilSocial> buscarPorId(Integer id) {
        return perfilSocialRepository.findById(id);
    }

    @Transactional
    public PerfilSocial salvar(PerfilSocial perfil) {
        return perfilSocialRepository.save(perfil);
    }

    @Transactional
    public void excluir(Integer id) {
        perfilSocialRepository.deleteById(id);
    }
}
