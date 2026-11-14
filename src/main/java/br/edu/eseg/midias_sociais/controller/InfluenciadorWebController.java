package br.edu.eseg.midias_sociais.controller;

import br.edu.eseg.midias_sociais.model.Influenciador;
import br.edu.eseg.midias_sociais.service.InfluenciadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/influenciadores")
@RequiredArgsConstructor
public class InfluenciadorWebController {

    private final InfluenciadorService influenciadorService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("influenciadores", influenciadorService.listarTodos());
        return "influenciadores/lista";
    }

    @GetMapping("/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("influenciador", new Influenciador());
        return "influenciadores/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Influenciador influenciador) {
        influenciadorService.salvar(influenciador);
        return "redirect:/influenciadores";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        influenciadorService.buscarPorId(id).ifPresent(i -> model.addAttribute("influenciador", i));
        return "influenciadores/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        influenciadorService.excluir(id);
        return "redirect:/influenciadores";
    }
}
