package br.edu.faculdade.bd.jpa.pkg005;

import br.edu.faculdade.bd.jpa.model.Curso;
import br.edu.faculdade.bd.jpa.model.Discente;
import br.edu.faculdade.bd.jpa.model.Imagem;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import jakarta.persistence.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Visualização web simples (HTML + JSON) para discentes e imagens usando JPA
 * puro.
 *
 * Rotas HTML: GET / -> lista discentes com miniaturas GET /discente?id=NN ->
 * página do discente GET /img?id=NN -> bytes do BLOB (Imagem) por DISCENTE_ID
 *
 * Rotas JSON: GET /api/discentes -> lista [{id,matricula,nome,curso}] GET
 * /api/discente?id=NN -> detalhe
 * {id,matricula,nome,curso,nome_arquivo,tipo_mime,criado_em}
 *
 * Servidor didático (não exponha na internet).
 */
public class WebViewer {

    // ======== JPA (PU) ========
    private static final String PU_NAME = "BDJPA";
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory(PU_NAME);

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception ignored) {
            }
        }
        WebViewer app = new WebViewer();
        app.start(port);
        System.out.println("Servidor iniciado em http://localhost:" + port + "  (Ctrl+C para parar)");
    }

    private void start(int port) throws IOException {
        HttpServer http = HttpServer.create(new InetSocketAddress(port), 0);

        // HTML
        http.createContext("/", this::handleIndex);
        http.createContext("/discente", this::handleDiscente);
        http.createContext("/img", this::handleImg);

        // JSON
        http.createContext("/api/discentes", this::handleApiDiscentes);
        http.createContext("/api/discente", this::handleApiDiscente);

        http.setExecutor(null);
        http.start();
    }

    // =======================================
    // Handlers HTML
    // =======================================
    private void handleIndex(HttpExchange ex) {
        EntityManager em = EMF.createEntityManager();
        try {
            // (defensivo) selecionar schema APP caso o default_schema não esteja ativo
            em.getTransaction().begin();
            em.createNativeQuery("CREATE SCHEMA IF NOT EXISTS APP").executeUpdate();
            em.createNativeQuery("SET SCHEMA APP").executeUpdate();
            em.getTransaction().commit();

            Map<String, String> q = queryParams(ex.getRequestURI());
            String filtro = q.getOrDefault("q", "").trim();
            boolean hasFiltro = !filtro.isBlank();

            String jpql
                    = "select d.id, d.matricula, d.nome, COALESCE(c.nome, '') "
                    + "from Discente d left join d.curso c ";
            if (hasFiltro) {
                jpql += "where UPPER(d.nome) like :p or UPPER(d.matricula) like :p or UPPER(COALESCE(c.nome,'')) like :p ";
            }
            jpql += "order by d.nome asc";

            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            if (hasFiltro) {
                query.setParameter("p", "%" + filtro.toUpperCase(Locale.ROOT) + "%");
            }
            List<Object[]> rows = query.getResultList();

            String html = """
                <!doctype html><html lang="pt-br"><head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>Discentes</title>
                  <style>
                    :root{--bg:#0f172a;--card:#111827;--muted:#94a3b8;--fg:#e5e7eb;--accent:#22d3ee}
                    body{margin:0;background:linear-gradient(135deg,#0f172a,#020617);font-family:system-ui,Segoe UI,Roboto,Arial}
                    .wrap{max-width:1100px;margin:40px auto;padding:0 16px}
                    h1{color:var(--fg);margin:0 0 16px;font-weight:700;letter-spacing:.3px}
                    .bar{display:flex;gap:12px;align-items:center;margin-bottom:18px}
                    input[type=text]{flex:1;padding:10px 12px;border-radius:12px;border:1px solid #1f2937;background:#0b1220;color:var(--fg);outline:none}
                    .grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(240px,1fr));gap:16px}
                    .card{background:rgba(17,24,39,.8);backdrop-filter:blur(4px);border:1px solid #1f2937;border-radius:16px;padding:12px;transition:transform .15s ease, box-shadow .15s ease}
                    .card:hover{transform:translateY(-2px);box-shadow:0 10px 20px rgba(0,0,0,.25)}
                    .thumb{width:100%%;height:160px;border-radius:12px;object-fit:cover;background:#0b1220;border:1px solid #0b1220}
                    .meta{margin-top:10px}
                    .nome{color:var(--fg);font-weight:600}
                    .sub{color:var(--muted);font-size:13px;margin-top:2px}
                    a.btn{display:inline-block;margin-top:10px;padding:8px 10px;border-radius:10px;background:#0b1220;color:var(--fg);border:1px solid #1f2937;text-decoration:none}
                    a.btn:hover{border-color:#334155}
                    .count{color:var(--muted);margin:8px 0 18px}
                    .footer{color:#64748b;font-size:12px;margin:24px 0}
                  </style>
                </head><body><div class="wrap">
                  <h1>Discentes</h1>
                  <form class="bar" method="get" action="/">
                    <input type="text" name="q" placeholder="Buscar por nome, matrícula ou curso..." value="%s">
                    <button class="btn" type="submit">Buscar</button>
                  </form>
                  <div class="count">Total: %d</div>
                  <div class="grid">
                """.formatted(escapeHtml(filtro), rows.size());

            StringBuilder cards = new StringBuilder();
            for (Object[] r : rows) {
                long id = ((Number) r[0]).longValue();
                String matricula = String.valueOf(r[1]);
                String nome = String.valueOf(r[2]);
                String curso = String.valueOf(r[3]);

                cards.append("""
                    <div class="card">
                      <img class="thumb" src="/img?id=%d"
                           onerror="this.src='data:image/svg+xml;utf8,%%3Csvg xmlns=%%22http://www.w3.org/2000/svg%%22 width=%%22360%%22 height=%%22160%%22%%3E%%3Crect width=%%22360%%22 height=%%22160%%22 fill=%%22%%230b1220%%22/%%3E%%3C/svg%%3E'">
                      <div class="meta">
                        <div class="nome">%s</div>
                        <div class="sub">Matrícula: %s</div>
                        <div class="sub">Curso: %s</div>
                        <a class="btn" href="/discente?id=%d">Abrir</a>
                      </div>
                    </div>
                    """.formatted(id, escapeHtml(nome), escapeHtml(matricula), escapeHtml(curso), id));
            }

            String end = """
                  </div>
                  <div class="footer">Visualização acadêmica simples • JPA + HTTP embutido • <a href="/api/discentes">API JSON</a></div>
                </div></body></html>
                """;

            sendHtml(ex, 200, html + cards + end);
        } catch (Exception e) {
            sendText(ex, 500, "Erro: " + e.getMessage());
        } finally {
            safeClose(em);
        }
    }

    private void handleDiscente(HttpExchange ex) {
        EntityManager em = EMF.createEntityManager();
        try {
            Map<String, String> q = queryParams(ex.getRequestURI());
            long id = parseLong(q.get("id"), -1);
            if (id <= 0) {
                sendText(ex, 400, "Parâmetro 'id' inválido");
                return;
            }

            Discente d = em.find(Discente.class, (int) id);
            if (d == null) {
                sendText(ex, 404, "Discente não encontrado");
                return;
            }
            Curso c = d.getCurso(); // pode ser LAZY

            Imagem i = findImagemByDiscenteId(em, d.getId());

            String nomeArq = "-";
            String mime = "-";
            String criadoFmt = "-";

            if (i != null) {
                if (i.getNomeArquivo() != null && !i.getNomeArquivo().isBlank()) {
                    nomeArq = i.getNomeArquivo();
                }
                if (i.getTipoMime() != null && !i.getTipoMime().isBlank()) {
                    mime = i.getTipoMime();
                }

                // formatação robusta (Timestamp / LocalDateTime / Date)
                String fmt = formatCriadoEm(i.getCriadoEm(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                if (fmt != null && !fmt.isBlank()) {
                    criadoFmt = fmt;
                }
            }

            String html = """
                <!doctype html><html lang="pt-br"><head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>Discente %d</title>
                  <style>
                    body{margin:0;background:#0b1220;color:#e5e7eb;font-family:system-ui,Segoe UI,Roboto,Arial}
                    .wrap{max-width:900px;margin:36px auto;padding:0 16px}
                    a{color:#22d3ee;text-decoration:none}
                    .card{background:#111827;border:1px solid #1f2937;border-radius:16px;overflow:hidden}
                    .img{width:100%%;height:420px;object-fit:cover;background:#0b1220}
                    .p{padding:16px}
                    .muted{color:#94a3b8}
                    .grid{display:grid;grid-template-columns:1fr 1fr;gap:12px}
                    .back{display:inline-block;margin-bottom:14px}
                  </style>
                </head><body><div class="wrap">
                  <a class="back" href="/">&larr; Voltar</a>
                  <div class="card">
                    <img class="img" src="/img?id=%d" alt="Imagem do discente">
                    <div class="p">
                      <h2 style="margin:0 0 8px">%s</h2>
                      <div class="grid">
                        <div><div class="muted">Matrícula</div><div>%s</div></div>
                        <div><div class="muted">Curso</div><div>%s</div></div>
                        <div><div class="muted">Arquivo</div><div>%s</div></div>
                        <div><div class="muted">MIME</div><div>%s</div></div>
                        <div><div class="muted">Criado</div><div>%s</div></div>
                      </div>
                    </div>
                  </div>
                </div></body></html>
                """.formatted(id, id,
                    escapeHtml(d.getNome()),
                    escapeHtml(d.getMatricula()),
                    escapeHtml(c != null ? nvl(c.getNome(), "-") : "-"),
                    escapeHtml(nomeArq),
                    escapeHtml(mime),
                    escapeHtml(criadoFmt));

            sendHtml(ex, 200, html);
        } catch (Exception e) {
            sendText(ex, 500, "Erro: " + e.getMessage());
        } finally {
            safeClose(em);
        }
    }

    private void handleImg(HttpExchange ex) {
        EntityManager em = EMF.createEntityManager();
        try {
            Map<String, String> q = queryParams(ex.getRequestURI());
            long id = parseLong(q.get("id"), -1);
            if (id <= 0) {
                sendText(ex, 400, "Parâmetro 'id' inválido");
                return;
            }

            Imagem img = findImagemByDiscenteId(em, (int) id);
            if (img == null || img.getBytes() == null) {
                String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='800' height='500'>"
                        + "<rect width='100%%' height='100%%' fill='#0b1220'/></svg>";
                sendBytes(ex, 200, "image/svg+xml", svg.getBytes(StandardCharsets.UTF_8));
                return;
            }
            String mime = nvl(img.getTipoMime(), "application/octet-stream");
            sendBytes(ex, 200, mime, img.getBytes());
        } catch (Exception e) {
            sendText(ex, 500, "Erro: " + e.getMessage());
        } finally {
            safeClose(em);
        }
    }

    // =======================================
    // Handlers JSON
    // =======================================
    private void handleApiDiscentes(HttpExchange ex) {
        EntityManager em = EMF.createEntityManager();
        try {
            Map<String, String> q = queryParams(ex.getRequestURI());
            String filtro = q.getOrDefault("q", "").trim();
            boolean hasFiltro = !filtro.isBlank();

            String jpql
                    = "select d.id, d.matricula, d.nome, COALESCE(c.nome,'') "
                    + "from Discente d left join d.curso c ";
            if (hasFiltro) {
                jpql += "where UPPER(d.nome) like :p or UPPER(d.matricula) like :p or UPPER(COALESCE(c.nome,'')) like :p ";
            }
            jpql += "order by d.nome asc";

            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            if (hasFiltro) {
                query.setParameter("p", "%" + filtro.toUpperCase(Locale.ROOT) + "%");
            }
            List<Object[]> rows = query.getResultList();

            StringBuilder json = new StringBuilder();
            json.append("[");
            boolean first = true;
            for (Object[] r : rows) {
                if (!first) {
                    json.append(",");
                }
                first = false;
                long id = ((Number) r[0]).longValue();
                String mat = String.valueOf(r[1]);
                String nome = String.valueOf(r[2]);
                String curso = String.valueOf(r[3]);
                json.append("{")
                        .append("\"id\":").append(id).append(",")
                        .append("\"matricula\":\"").append(jsonEscape(mat)).append("\",")
                        .append("\"nome\":\"").append(jsonEscape(nome)).append("\",")
                        .append("\"curso\":\"").append(jsonEscape(curso)).append("\"")
                        .append("}");
            }
            json.append("]");
            sendJson(ex, 200, json.toString());
        } catch (Exception e) {
            sendJson(ex, 500, "{\"erro\":\"" + jsonEscape(e.getMessage()) + "\"}");
        } finally {
            safeClose(em);
        }
    }

    private void handleApiDiscente(HttpExchange ex) {
        EntityManager em = EMF.createEntityManager();
        try {
            Map<String, String> q = queryParams(ex.getRequestURI());
            long id = parseLong(q.get("id"), -1);
            if (id <= 0) {
                sendJson(ex, 400, "{\"erro\":\"id inválido\"}");
                return;
            }

            Discente d = em.find(Discente.class, (int) id);
            if (d == null) {
                sendJson(ex, 404, "{\"erro\":\"não encontrado\"}");
                return;
            }
            Curso c = d.getCurso();

            Imagem i = findImagemByDiscenteId(em, d.getId());
            String arq = (i == null ? null : i.getNomeArquivo());
            String mime = (i == null ? null : i.getTipoMime());

            String cri = null;
            if (i != null) {
                cri = formatCriadoEm(i.getCriadoEm(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            StringBuilder json = new StringBuilder();
            json.append("{")
                    .append("\"id\":").append(d.getId()).append(",")
                    .append("\"matricula\":\"").append(jsonEscape(d.getMatricula())).append("\",")
                    .append("\"nome\":\"").append(jsonEscape(d.getNome())).append("\",")
                    .append("\"curso\":\"").append(jsonEscape(c != null ? nvl(c.getNome(), "-") : "-")).append("\",")
                    .append("\"nome_arquivo\":").append(arq == null ? "null" : "\"" + jsonEscape(arq) + "\"").append(",")
                    .append("\"tipo_mime\":").append(mime == null ? "null" : "\"" + jsonEscape(mime) + "\"").append(",")
                    .append("\"criado_em\":").append(cri == null ? "null" : "\"" + jsonEscape(cri) + "\"")
                    .append("}");

            sendJson(ex, 200, json.toString());
        } catch (Exception e) {
            sendJson(ex, 500, "{\"erro\":\"" + jsonEscape(e.getMessage()) + "\"}");
        } finally {
            safeClose(em);
        }
    }

    // =======================================
    // JPA helpers
    // =======================================
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

    private static void safeClose(EntityManager em) {
        try {
            if (em != null && em.isOpen()) {
                em.close();
            }
        } catch (Exception ignore) {
        }
    }

    // =======================================
    // Date/Time helper robusto
    // =======================================
    /**
     * Formata 'CRIADO_EM' vindo como LocalDateTime, Timestamp, Date ou texto.
     */
    private static String formatCriadoEm(Object value, DateTimeFormatter fmt) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDateTime ldt) {
            return ldt.format(fmt);
        }
        if (value instanceof Timestamp ts) {
            return ts.toLocalDateTime().format(fmt);
        }
        if (value instanceof Date d) {
            return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()).format(fmt);
        }
        if (value instanceof CharSequence cs) {
            return cs.toString();
        }
        return String.valueOf(value);
    }

    // =======================================
    // HTTP/HTML/JSON util
    // =======================================
    private static void sendHtml(HttpExchange ex, int code, String html) {
        sendBytes(ex, code, "text/html; charset=utf-8", html.getBytes(StandardCharsets.UTF_8));
    }

    private static void sendText(HttpExchange ex, int code, String text) {
        sendBytes(ex, code, "text/plain; charset=utf-8", text.getBytes(StandardCharsets.UTF_8));
    }

    private static void sendJson(HttpExchange ex, int code, String body) {
        sendBytes(ex, code, "application/json; charset=utf-8", body.getBytes(StandardCharsets.UTF_8));
    }

    private static void sendBytes(HttpExchange ex, int code, String contentType, byte[] body) {
        try {
            Headers h = ex.getResponseHeaders();
            h.set("Content-Type", contentType);
            h.set("Cache-Control", "no-store");
            if (contentType.startsWith("application/json")) {
                h.set("Access-Control-Allow-Origin", "*");
            }
            ex.sendResponseHeaders(code, body.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(body);
            }
        } catch (IOException ignored) {
        } finally {
            try {
                ex.close();
            } catch (Exception ignore) {
            }
        }
    }

    // =======================================
    // Misc helpers
    // =======================================
    private static Map<String, String> queryParams(URI uri) {
        String q = uri.getRawQuery();
        if (q == null || q.isBlank()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(q.split("&"))
                .map(kv -> kv.split("=", 2))
                .filter(a -> a.length == 2)
                .collect(Collectors.toMap(
                        a -> urlDecode(a[0]),
                        a -> urlDecode(a[1]),
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }

    private static String urlDecode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private static long parseLong(String s, long def) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String jsonEscape(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder b = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' ->
                    b.append("\\\"");
                case '\\' ->
                    b.append("\\\\");
                case '\b' ->
                    b.append("\\b");
                case '\f' ->
                    b.append("\\f");
                case '\n' ->
                    b.append("\\n");
                case '\r' ->
                    b.append("\\r");
                case '\t' ->
                    b.append("\\t");
                default -> {
                    if (c < 0x20) {
                        b.append(String.format("\\u%04x", (int) c));
                    } else {
                        b.append(c);
                    }
                }
            }
        }
        return b.toString();
    }

    private static String nvl(String v, String alt) {
        return (v == null || v.isBlank()) ? alt : v;
    }
}
