package br.edu.eseg.midias_sociais.controller;

import br.edu.eseg.midias_sociais.model.Post;
import br.edu.eseg.midias_sociais.service.CampanhaService;
import br.edu.eseg.midias_sociais.service.PerfilSocialService;
import br.edu.eseg.midias_sociais.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostWebController {

    private final PostService postService;
    private final PerfilSocialService perfilSocialService;
    private final CampanhaService campanhaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("posts", postService.listarTodos());
        return "posts/lista";
    }

    @GetMapping("/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("perfis", perfilSocialService.listarTodos());
        model.addAttribute("campanhas", campanhaService.listarTodos());
        return "posts/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Post post) {
        postService.salvar(post);
        return "redirect:/posts";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        postService.buscarPorId(id).ifPresent(p -> model.addAttribute("post", p));
        model.addAttribute("perfis", perfilSocialService.listarTodos());
        model.addAttribute("campanhas", campanhaService.listarTodos());
        return "posts/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        postService.excluir(id);
        return "redirect:/posts";
    }
}
