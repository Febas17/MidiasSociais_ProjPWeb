package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.Discente;
import br.edu.faculdade.academico.model.Imagem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class DiscenteFotoService {
    private static final long LIMITE_BYTES = 2L * 1024 * 1024;

    @PersistenceContext
    private EntityManager em;

    public void validar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) return;
        if (arquivo.getSize() > LIMITE_BYTES) {
            throw new IllegalArgumentException("A foto deve ter até 2 MB.");
        }
        String tipo = arquivo.getContentType();
        if (!"image/png".equals(tipo) && !"image/jpeg".equals(tipo)) {
            throw new IllegalArgumentException("Envie uma imagem PNG ou JPEG.");
        }
        // Verificação adicional da assinatura dos bytes; não confiar apenas no Content-Type.
        try {
            byte[] inicio = arquivo.getInputStream().readNBytes(8);
            boolean png = inicio.length >= 8 && (inicio[0] & 0xff) == 137 &&
                (inicio[1] & 0xff) == 80 && (inicio[2] & 0xff) == 78 &&
                (inicio[3] & 0xff) == 71 && (inicio[4] & 0xff) == 13 &&
                (inicio[5] & 0xff) == 10 && (inicio[6] & 0xff) == 26 &&
                (inicio[7] & 0xff) == 10;
            boolean jpeg = inicio.length >= 3 && (inicio[0] & 0xff) == 255 &&
                (inicio[1] & 0xff) == 216 && (inicio[2] & 0xff) == 255;
            if (!(png && "image/png".equals(tipo)) &&
                !(jpeg && "image/jpeg".equals(tipo))) {
                throw new IllegalArgumentException("Arquivo de imagem inválido.");
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Não foi possível ler a foto.", ex);
        }
    }

    @Transactional
    public void salvar(UUID discenteId, MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) return; // Edição sem arquivo: manter foto.
        validar(arquivo);
        Discente discente = em.find(Discente.class, discenteId);
        if (discente == null) throw new IllegalArgumentException("Discente não encontrado.");
        List<Imagem> existentes = em.createQuery(
                "select i from Imagem i where i.discente.id = :id", Imagem.class)
            .setParameter("id", discenteId).setMaxResults(1).getResultList();
        Imagem imagem = existentes.isEmpty() ? new Imagem() : existentes.get(0);
        try {
            imagem.setBytes(arquivo.getBytes());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Erro ao ler a foto.", ex);
        }
        boolean png = "image/png".equals(arquivo.getContentType());
        imagem.setTipoMime(png ? "image/png" : "image/jpeg");
        imagem.setNomeArquivo("discente_" + discenteId + (png ? ".png" : ".jpg"));
        if (existentes.isEmpty()) {
            imagem.setDiscente(discente);
            em.persist(imagem);
        }
    }

    @Transactional(readOnly = true)
    public Imagem buscar(UUID discenteId) {
        List<Imagem> imagens = em.createQuery(
                "select i from Imagem i where i.discente.id = :id", Imagem.class)
            .setParameter("id", discenteId).setMaxResults(1).getResultList();
        return imagens.isEmpty() ? null : imagens.get(0);
    }

    @Transactional
    public void excluir(UUID discenteId) {
        em.createQuery("delete from Imagem i where i.discente.id = :id")
            .setParameter("id", discenteId).executeUpdate();
    }
}
