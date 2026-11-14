package br.edu.eseg.midias_sociais.controller;

import br.edu.eseg.midias_sociais.model.Comentario;
import br.edu.eseg.midias_sociais.service.ComentarioService;
import br.edu.eseg.midias_sociais.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioWebController {

    private final ComentarioService comentarioService;
    private final PostService postService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("comentarios", comentarioService.listarTodos());
        return "comentarios/lista";
    }

    @GetMapping("/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("comentario", new Comentario());
        model.addAttribute("posts", postService.listarTodos());
        return "comentarios/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Comentario comentario) {
        comentarioService.salvar(comentario);
        return "redirect:/comentarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        comentarioService.buscarPorId(id).ifPresent(c -> model.addAttribute("comentario", c));
        model.addAttribute("posts", postService.listarTodos());
        return "comentarios/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        comentarioService.excluir(id);
        return "redirect:/comentarios";
    }
}
