package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.model.Imagem;
import br.edu.faculdade.academico.model.Professor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProfessorFotoService {
    private static final int MAX_BYTES = 2 * 1024 * 1024;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void salvar(UUID professorId, MultipartFile foto) throws IOException {
        if (foto == null || foto.isEmpty()) return;
        if (foto.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("A foto deve ter no máximo 2 MB.");
        }
        byte[] bytes = foto.getBytes();
        String tipo = detectarTipo(bytes);
        if (tipo == null) {
            throw new IllegalArgumentException("Envie uma imagem PNG ou JPEG válida.");
        }
        Professor professor = em.find(Professor.class, professorId);
        if (professor == null) throw new IllegalArgumentException("Professor não encontrado.");
        List<Imagem> existentes = em.createQuery(
                "select i from Imagem i where i.professor.id = :id", Imagem.class)
            .setParameter("id", professorId).setMaxResults(1).getResultList();
        Imagem imagem = existentes.isEmpty() ? new Imagem() : existentes.get(0);
        if (existentes.isEmpty()) {
            imagem.setProfessor(professor);
            imagem.setDiscente(null);
        }
        imagem.setNomeArquivo("professor_" + professorId + (tipo.equals("image/png") ? ".png" : ".jpg"));
        imagem.setTipoMime(tipo);
        imagem.setBytes(bytes);
        if (existentes.isEmpty()) em.persist(imagem);
    }

    @Transactional(readOnly = true)
    public Imagem buscar(UUID professorId) {
        List<Imagem> imagens = em.createQuery(
                "select i from Imagem i where i.professor.id = :id", Imagem.class)
            .setParameter("id", professorId).setMaxResults(1).getResultList();
        return imagens.isEmpty() ? null : imagens.get(0);
    }

    @Transactional
    public void excluir(UUID professorId) {
        List<Imagem> imagens = em.createQuery(
                "select i from Imagem i where i.professor.id = :id", Imagem.class)
            .setParameter("id", professorId).getResultList();
        imagens.forEach(em::remove);
    }

    private static String detectarTipo(byte[] b) {
        if (b.length >= 8 && (b[0] & 255) == 137 && b[1] == 80 && b[2] == 78
                && b[3] == 71 && b[4] == 13 && b[5] == 10 && b[6] == 26 && b[7] == 10)
            return "image/png";
        if (b.length >= 4 && (b[0] & 255) == 255 && (b[1] & 255) == 216
                && (b[b.length - 2] & 255) == 255 && (b[b.length - 1] & 255) == 217)
            return "image/jpeg";
        return null;
    }
}
