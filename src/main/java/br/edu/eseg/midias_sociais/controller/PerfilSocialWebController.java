package br.edu.eseg.midias_sociais.controller;

import br.edu.eseg.midias_sociais.model.PerfilSocial;
import br.edu.eseg.midias_sociais.service.ContaService;
import br.edu.eseg.midias_sociais.service.PerfilSocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/perfis")
@RequiredArgsConstructor
public class PerfilSocialWebController {

    private final PerfilSocialService perfilSocialService;
    private final ContaService contaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("perfis", perfilSocialService.listarTodos());
        return "perfis/lista";
    }

    @GetMapping("/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("perfil", new PerfilSocial());
        model.addAttribute("contas", contaService.listarTodos());
        return "perfis/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute PerfilSocial perfil) {
        perfilSocialService.salvar(perfil);
        return "redirect:/perfis";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        perfilSocialService.buscarPorId(id).ifPresent(p -> model.addAttribute("perfil", p));
        model.addAttribute("contas", contaService.listarTodos());
        return "perfis/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        perfilSocialService.excluir(id);
        return "redirect:/perfis";
    }
}
