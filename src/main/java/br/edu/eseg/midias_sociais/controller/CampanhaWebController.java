package br.edu.eseg.midias_sociais.controller;

import br.edu.eseg.midias_sociais.model.Campanha;
import br.edu.eseg.midias_sociais.service.CampanhaService;
import br.edu.eseg.midias_sociais.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/campanhas")
@RequiredArgsConstructor
public class CampanhaWebController {

    private final CampanhaService campanhaService;
    private final ContaService contaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("campanhas", campanhaService.listarTodos());
        return "campanhas/lista";
    }

    @GetMapping("/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("campanha", new Campanha());
        model.addAttribute("contas", contaService.listarTodos());
        return "campanhas/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Campanha campanha) {
        campanhaService.salvar(campanha);
        return "redirect:/campanhas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        campanhaService.buscarPorId(id).ifPresent(c -> model.addAttribute("campanha", c));
        model.addAttribute("contas", contaService.listarTodos());
        return "campanhas/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        campanhaService.excluir(id);
        return "redirect:/campanhas";
    }
}
