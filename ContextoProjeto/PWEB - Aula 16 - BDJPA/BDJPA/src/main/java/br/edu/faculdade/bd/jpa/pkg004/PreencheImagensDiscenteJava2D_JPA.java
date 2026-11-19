package br.edu.faculdade.bd.jpa.pkg004;


import br.edu.faculdade.bd.jpa.model.Discente;
import br.edu.faculdade.bd.jpa.model.Imagem;
import jakarta.persistence.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PreencheImagensDiscenteJava2D_JPA {

    private static final String PU_NAME = "BDJPA";

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        int width = 1024, height = 1024;
        boolean onlyMissing = false;

        if (args.length >= 1 && args[0] != null && args[0].contains("x")) {
            String[] wh = args[0].toLowerCase(Locale.ROOT).split("x");
            width  = Math.max(256, parseIntSafe(wh[0], 1024));
            height = Math.max(256, parseIntSafe(wh[1], 1024));
        }
        if (args.length >= 2 && "only-missing".equalsIgnoreCase(args[1])) {
            onlyMissing = true;
        }

        new PreencheImagensDiscenteJava2D_JPA().run(width, height, onlyMissing);
    }

    private void run(int width, int height, boolean onlyMissing) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // (defensivo) garante schema APP ativo se default_schema não estiver setado
            em.createNativeQuery("CREATE SCHEMA IF NOT EXISTS APP").executeUpdate();
            em.createNativeQuery("SET SCHEMA APP").executeUpdate();

            // Busca todos os discentes (ou só os que ainda não têm imagem)
            String jpql = "select d from Discente d";
            if (onlyMissing) {
                jpql += " where not exists (select 1 from Imagem i where i.discente.id = d.id)";
            }
            List<Discente> discentes = em.createQuery(jpql, Discente.class).getResultList();
            System.out.println("Discentes a processar: " + discentes.size());

            int count = 0;
            for (Discente d : discentes) {
                // Gera avatar PNG (círculo colorido + iniciais)
                byte[] png = renderAvatarPNG(width, height, d.getNome(), d.getMatricula());

                // Upsert em APP.IMAGEM por DISCENTE_ID
                upsertImagem(em, d, "discente_" + d.getId() + ".png", "image/png", png);
                System.out.printf("DISCENTE %d -> avatar gerado e salvo%n", d.getId());

                if (++count % 25 == 0) {
                    em.flush();
                    em.clear();
                }
            }

            tx.commit();
            System.out.println("Total processado: " + count);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace(System.err);
        } finally {
            em.close();
            emf.close();
        }
    }

    /** Upsert por DISCENTE_ID: se existir Imagem para o discente, atualiza; senão, insere. */
    private void upsertImagem(EntityManager em, Discente discente, String nomeArquivo, String mime, byte[] bytes) {
        Imagem existente = findImagemByDiscenteId(em, discente.getId());
        if (existente == null) {
            Imagem nova = new Imagem();
            nova.setDiscente(discente);       // mesmo contexto; d está gerenciado
            nova.setNomeArquivo(nomeArquivo);
            nova.setTipoMime(mime);
            nova.setBytes(bytes);
            em.persist(nova);
        } else {
            boolean changed = false;
            if (!nomeArquivo.equals(existente.getNomeArquivo())) { existente.setNomeArquivo(nomeArquivo); changed = true; }
            if (!mime.equals(existente.getTipoMime()))           { existente.setTipoMime(mime);           changed = true; }
            // sempre substitui a imagem
            existente.setBytes(bytes);
            changed = true;

            if (changed) em.merge(existente);
        }
    }

    private Imagem findImagemByDiscenteId(EntityManager em, Integer discenteId) {
        try {
            return em.createQuery(
                    "select i from Imagem i where i.discente.id = :id", Imagem.class)
                .setParameter("id", discenteId)
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // ======================== RENDER (Java2D) ==========================
    private static byte[] renderAvatarPNG(int w, int h, String nome, String matricula) throws Exception {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,       RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);

            // Fundo neutro
            g.setColor(new Color(245, 247, 250));
            g.fillRect(0, 0, w, h);

            // Avatar (círculo) centralizado
            int avatarSize = Math.min(w, h) * 2 / 3;
            int avatarX    = (w - avatarSize) / 2;
            int avatarY    = (h - avatarSize) / 2;

            // Cor baseada na matrícula
            Color avatarColor = colorFromString(Objects.toString(matricula, ""));
            g.setColor(avatarColor);
            g.fillOval(avatarX, avatarY, avatarSize, avatarSize);

            // Borda sutil
            g.setStroke(new BasicStroke(Math.max(2f, avatarSize / 64f)));
            g.setColor(new Color(0, 0, 0, 40));
            g.drawOval(avatarX, avatarY, avatarSize, avatarSize);

            // Iniciais no centro
            String iniciais = initials(nome);
            int fontPx = Math.max(36, avatarSize / 3);
            g.setFont(new Font("SansSerif", Font.BOLD, fontPx));
            g.setColor(Color.WHITE);
            drawCenteredString(g, iniciais, avatarX, avatarY, avatarSize, avatarSize);
        } finally {
            g.dispose();
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", bos);
            return bos.toByteArray();
        }
    }

    private static void drawCenteredString(Graphics2D g, String text, int x, int y, int w, int h) {
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle rect = g.getFont().createGlyphVector(frc, text).getPixelBounds(null, 0, 0);
        int tx = x + (w - rect.width) / 2 - rect.x;
        int ty = y + (h - rect.height) / 2 - rect.y;
        g.drawString(text, tx, ty);
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    private static String initials(String nome) {
        if (nome == null || nome.isBlank()) return "AA";
        String[] parts = nome.trim().split("\\s+");
        String a = parts[0].substring(0, 1).toUpperCase(Locale.ROOT);
        String b = parts.length > 1 ? parts[parts.length - 1].substring(0, 1).toUpperCase(Locale.ROOT) : "A";
        return a + b;
    }

    /** Gera uma cor estável a partir de uma string (matrícula). */
    private static Color colorFromString(String s) {
        if (s == null) s = "";
        int h = 0;
        for (int i = 0; i < s.length(); i++) h = 31 * h + s.charAt(i);
        float hue = (h & 0x7fffffff) % 360 / 360f; // 0..1
        return hsl(hue, 0.55f, 0.55f);
        }

    private static Color hsl(float h, float s, float l) {
        float q = (l < 0.5f) ? l * (1 + s) : (l + s - l * s);
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

    private static int clamp255(float v) {
        return Math.min(255, Math.max(0, Math.round(v)));
    }
}
