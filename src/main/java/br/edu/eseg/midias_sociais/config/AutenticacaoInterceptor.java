package br.edu.eseg.midias_sociais.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor de autenticação e controle de permissões por perfil.
 * Garante que rotas protegidas exijam login e que áreas administrativas (como /usuarios)
 * sejam restritas ao perfil ADMIN.
 */
@Component
public class AutenticacaoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Recursos públicos permitidos sem autenticação
        if (uri.startsWith("/login") || uri.startsWith("/logout") || uri.startsWith("/css/")
                || uri.startsWith("/js/") || uri.startsWith("/images/") || uri.startsWith("/h2-console")
                || uri.startsWith("/error")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendRedirect("/login");
            return false;
        }

        // Controle de permissões RBAC:
        // Apenas ADMIN pode acessar o módulo de gestão de usuários
        if (uri.startsWith("/usuarios")) {
            String perfil = (String) session.getAttribute("usuarioPerfil");
            if (!"ADMIN".equals(perfil)) {
                response.sendRedirect("/?semPermissao=true");
                return false;
            }
        }

        return true;
    }
}
