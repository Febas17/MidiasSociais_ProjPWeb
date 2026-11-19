package br.edu.faculdade.academico.controller.web;

import br.edu.faculdade.academico.model.Departamento;
import br.edu.faculdade.academico.service.DepartamentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/{tema:basico|bootstrap}/departamentos")
public class DepartamentoWebController {
    private final DepartamentoService service;

    public DepartamentoWebController(DepartamentoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@PathVariable String tema, Model model) {
        model.addAttribute("departamentos", service.listar());
        return tema + "/departamentos/lista";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable String tema, Model model) {
        model.addAttribute("departamento", new Departamento());
        return tema + "/departamentos/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String tema, @PathVariable UUID id, Model model) {
        model.addAttribute("departamento", service.buscar(id));
        return tema + "/departamentos/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@PathVariable String tema, @ModelAttribute Departamento departamento, RedirectAttributes flash) {
        if (departamento.getNome() == null || departamento.getNome().isBlank() || departamento.getSigla() == null || departamento.getSigla().isBlank())
            throw new IllegalArgumentException("Nome e sigla são obrigatórios");
        if (departamento.getId() == null) service.cadastrar(departamento);
        else service.atualizar(departamento.getId(), departamento);
        flash.addFlashAttribute("mensagem", "Departamento salvo com sucesso!");
        return "redirect:/" + tema + "/departamentos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String tema, @PathVariable UUID id, RedirectAttributes flash) {
        service.excluir(id);
        flash.addFlashAttribute("mensagem", "Departamento excluído.");
        return "redirect:/" + tema + "/departamentos";
    }
}
