package br.edu.eseg.midias_sociais.controller;

import br.edu.eseg.midias_sociais.model.Conta;
import br.edu.eseg.midias_sociais.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaWebController {

    private final ContaService contaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("contas", contaService.listarTodos());
        return "contas/lista";
    }

    @GetMapping("/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("conta", new Conta());
        return "contas/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Conta conta) {
        contaService.salvar(conta);
        return "redirect:/contas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        contaService.buscarPorId(id).ifPresent(c -> model.addAttribute("conta", c));
        return "contas/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        contaService.excluir(id);
        return "redirect:/contas";
    }
}
