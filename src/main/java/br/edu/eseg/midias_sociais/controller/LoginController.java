package br.edu.eseg.midias_sociais.controller;

import br.edu.eseg.midias_sociais.model.Usuario;
import br.edu.eseg.midias_sociais.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String telaLogin(@RequestParam(value = "erro", required = false) String erro,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model, HttpSession session) {
        if (session != null && session.getAttribute("usuarioLogado") != null) {
            return "redirect:/";
        }
        if (erro != null) {
            model.addAttribute("mensagemErro", "Credenciais inválidas ou usuário inativo.");
        }
        if (logout != null) {
            model.addAttribute("mensagemSucesso", "Sessão encerrada com sucesso.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam("username") String username,
                             @RequestParam("senha") String senha,
                             HttpSession session,
                             Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            usuarioOpt = usuarioRepository.findByEmail(username);
        }

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (Boolean.TRUE.equals(usuario.getAtivo()) && usuario.getSenha().equals(senha)) {
                session.setAttribute("usuarioLogado", usuario);
                session.setAttribute("usuarioNome", usuario.getNome());
                session.setAttribute("usuarioPerfil", usuario.getPerfil().name());
                session.setAttribute("usuarioPerfilDescricao", usuario.getPerfil().getDescricao());
                return "redirect:/";
            }
        }

        return "redirect:/login?erro=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout=true";
    }
}
