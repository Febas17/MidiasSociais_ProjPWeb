package br.edu.faculdade.carga;

import br.edu.faculdade.academico.model.Discente;
import br.edu.faculdade.academico.model.Professor;
import br.edu.faculdade.academico.model.Imagem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Cria avatares didáticos para discentes e professores.
 * Não substitui fotos existentes por padrão.
 * Exemplos: 512x512 only-missing; professores only-missing; todos replace-all.
 */
public class Carga008Imagem {

    private static final String PU_NAME = "BDJPA";
    private static final int TAMANHO_LOTE = 25;

    public static void main(String[] args) throws Exception {
        int largura = 512;
        int altura = 512;
        boolean somenteAusentes = true;
        boolean incluirDiscentes = true;
        boolean incluirProfessores = true;

        for (String arg : args) {
            if (arg.matches("(?i)\\d+x\\d+")) {
                String[] dimensoes = arg.toLowerCase(Locale.ROOT).split("x");
                largura = Math.max(256, Integer.parseInt(dimensoes[0]));
                altura = Math.max(256, Integer.parseInt(dimensoes[1]));
            } else if ("only-missing".equalsIgnoreCase(arg)) {
                somenteAusentes = true;
            } else if ("replace-all".equalsIgnoreCase(arg)) {
                somenteAusentes = false;
            } else if ("professores".equalsIgnoreCase(arg)) {
                incluirDiscentes = false;
                incluirProfessores = true;
            } else if ("discentes".equalsIgnoreCase(arg)) {
                incluirDiscentes = true;
                incluirProfessores = false;
            } else if ("todos".equalsIgnoreCase(arg)) {
                incluirDiscentes = true;
                incluirProfessores = true;
            } else {
                throw new IllegalArgumentException("Argumento desconhecido: " + arg);
            }
        }

        if (largura > 2048 || altura > 2048) {
            throw new IllegalArgumentException("Dimensão máxima: 2048x2048");
        }
        new Carga008Imagem().executar(
                largura, altura, somenteAusentes, incluirDiscentes, incluirProfessores);
    }

    private void executar(int largura, int altura, boolean somenteAusentes,
                          boolean incluirDiscentes, boolean incluirProfessores) throws IOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME);
        try {
            int total = 0;
            if (incluirDiscentes) {
                total += processar(emf, "DISCENTE", largura, altura, somenteAusentes);
            }
            if (incluirProfessores) {
                total += processar(emf, "PROFESSOR", largura, altura, somenteAusentes);
            }
            System.out.println("Total de avatares inseridos/atualizados: " + total);
        } finally {
            emf.close();
        }
    }

    private int processar(EntityManagerFactory emf, String tipo, int largura, int altura,
                          boolean somenteAusentes) throws IOException {
        List<UUID> ids;
        EntityManager consulta = emf.createEntityManager();
        try {
            String jpql;
            if (tipo.equals("DISCENTE")) {
                jpql = "select d.id from Discente d";
                if (somenteAusentes) {
                    jpql += " where not exists (select i.id from Imagem i where i.discente = d)";
                }
                jpql += " order by d.matricula";
            } else {
                jpql = "select p.id from Professor p";
                if (somenteAusentes) {
                    jpql += " where not exists (select i.id from Imagem i where i.professor = p)";
                }
                jpql += " order by p.registro";
            }
            ids = consulta.createQuery(jpql, UUID.class).getResultList();
        } finally {
            consulta.close();
        }

        System.out.printf("%s: %d pessoas selecionadas (%s)%n", tipo, ids.size(),
                somenteAusentes ? "only-missing" : "replace-all");

        int total = 0;
        for (int inicio = 0; inicio < ids.size(); inicio += TAMANHO_LOTE) {
            int fim = Math.min(inicio + TAMANHO_LOTE, ids.size());
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                for (UUID id : ids.subList(inicio, fim)) {
                    Discente discente = null;
                    Professor professor = null;
                    String nome;
                    String identificador;
                    if (tipo.equals("DISCENTE")) {
                        discente = em.find(Discente.class, id);
                        if (discente == null) continue;
                        nome = discente.getNome();
                        identificador = discente.getMatricula();
                    } else {
                        professor = em.find(Professor.class, id);
                        if (professor == null) continue;
                        nome = professor.getNome();
                        identificador = professor.getRegistro();
                    }

                    List<Imagem> encontradas = tipo.equals("DISCENTE")
                        ? em.createQuery("select i from Imagem i where i.discente.id = :id", Imagem.class)
                            .setParameter("id", id).setMaxResults(1).getResultList()
                        : em.createQuery("select i from Imagem i where i.professor.id = :id", Imagem.class)
                            .setParameter("id", id).setMaxResults(1).getResultList();

                    if (somenteAusentes && !encontradas.isEmpty()) continue;

                    byte[] png = renderAvatarPNG(largura, altura, nome, identificador);
                    Imagem imagem = encontradas.isEmpty() ? new Imagem() : encontradas.get(0);
                    if (encontradas.isEmpty()) {
                        if (discente != null) imagem.setDiscente(discente);
                        else imagem.setProfessor(professor);
                    }
                    imagem.setNomeArquivo(tipo.toLowerCase(Locale.ROOT) + "_" + id + ".png");
                    imagem.setTipoMime("image/png");
                    imagem.setBytes(png);
                    if (encontradas.isEmpty()) em.persist(imagem);
                    total++;
                }
                tx.commit();
                System.out.printf("%s: lote %d concluído (%d/%d)%n", tipo,
                        inicio / TAMANHO_LOTE + 1, fim, ids.size());
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new IllegalStateException("Falha ao salvar avatares " + tipo
                    + " no lote " + (inicio / TAMANHO_LOTE + 1), e);
            } finally {
                em.close();
            }
        }
        return total;
    }

    private static byte[] renderAvatarPNG(int w, int h, String nome, String matricula)
            throws IOException {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            g.setColor(new Color(245, 247, 250));
            g.fillRect(0, 0, w, h);

            int avatarSize = Math.min(w, h) * 2 / 3;
            int x = (w - avatarSize) / 2;
            int y = (h - avatarSize) / 2;

            g.setColor(colorFromString(Objects.toString(matricula, "")));
            g.fillOval(x, y, avatarSize, avatarSize);
            g.setStroke(new BasicStroke(Math.max(2f, avatarSize / 64f)));
            g.setColor(new Color(0, 0, 0, 40));
            g.drawOval(x, y, avatarSize, avatarSize);

            g.setFont(new Font("SansSerif", Font.BOLD, Math.max(36, avatarSize / 3)));
            g.setColor(Color.WHITE);
            drawCenteredString(g, initials(nome), x, y, avatarSize, avatarSize);
        } finally {
            g.dispose();
        }

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (!ImageIO.write(img, "png", output)) {
                throw new IOException("Não foi possível codificar a imagem PNG.");
            }
            return output.toByteArray();
        }
    }

    private static void drawCenteredString(Graphics2D g, String text,
                                           int x, int y, int w, int h) {
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle rect = g.getFont().createGlyphVector(frc, text)
                .getPixelBounds(null, 0, 0);
        int tx = x + (w - rect.width) / 2 - rect.x;
        int ty = y + (h - rect.height) / 2 - rect.y;
        g.drawString(text, tx, ty);
    }

    private static String initials(String nome) {
        if (nome == null || nome.isBlank()) {
            return "AA";
        }
        String[] partes = nome.trim().split("\\s+");
        String primeira = partes[0].substring(0, 1).toUpperCase(Locale.ROOT);
        String ultima = partes.length > 1
                ? partes[partes.length - 1].substring(0, 1).toUpperCase(Locale.ROOT)
                : "A";
        return primeira + ultima;
    }

    private static Color colorFromString(String text) {
        int hash = 0;
        for (int i = 0; i < text.length(); i++) {
            hash = 31 * hash + text.charAt(i);
        }
        float hue = (hash & 0x7fffffff) % 360 / 360f;
        return hsl(hue, 0.55f, 0.55f);
    }

    private static Color hsl(float h, float s, float l) {
        float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
        float p = 2 * l - q;
        float r = hue2rgb(p, q, h + 1f / 3f);
        float g = hue2rgb(p, q, h);
        float b = hue2rgb(p, q, h - 1f / 3f);
        return new Color(clamp255(r * 255), clamp255(g * 255), clamp255(b * 255));
    }

    private static float hue2rgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1f / 6f) return p + (q - p) * 6 * t;
        if (t < 1f / 2f) return q;
        if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6;
        return p;
    }

    private static int clamp255(float value) {
        return Math.min(255, Math.max(0, Math.round(value)));
    }
}
