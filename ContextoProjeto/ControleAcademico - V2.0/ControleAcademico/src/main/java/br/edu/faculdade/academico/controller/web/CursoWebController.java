package br.edu.faculdade.academico.controller.web;

import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/{tema:basico|bootstrap}/cursos")
public class CursoWebController {
    private final CursoService cursos;
    private final DepartamentoService departamentos;

    public CursoWebController(CursoService cursos, DepartamentoService departamentos) {
        this.cursos = cursos;
        this.departamentos = departamentos;
    }

    @GetMapping
    public String listar(@PathVariable String tema, Model model) {
        model.addAttribute("cursos", cursos.listar());
        return tema + "/cursos/lista";
    }

    private String form(String tema, Curso c, Model m) {
        m.addAttribute("curso", c);
        m.addAttribute("departamentos", departamentos.listar());
        return tema + "/cursos/formulario";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable String tema, Model m) {
        return form(tema, new Curso(), m);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String tema, @PathVariable UUID id, Model m) {
        return form(tema, cursos.buscar(id), m);
    }

    @PostMapping("/salvar")
    public String salvar(@PathVariable String tema, @RequestParam(required = false) UUID id, @RequestParam String nome, @RequestParam String sigla, @RequestParam UUID departamentoId, RedirectAttributes flash) {
        Curso curso = new Curso();
        curso.setNome(nome);
        curso.setSigla(sigla);
        Departamento departamento = new Departamento();
        departamento.setId(departamentoId);
        curso.setDepartamento(departamento);
        if (id == null) cursos.cadastrar(curso);
        else cursos.atualizar(id, curso);
        flash.addFlashAttribute("mensagem", "Curso salvo com sucesso!");
        return "redirect:/" + tema + "/cursos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String tema, @PathVariable UUID id, RedirectAttributes flash) {
        cursos.excluir(id);
        flash.addFlashAttribute("mensagem", "Curso excluído.");
        return "redirect:/" + tema + "/cursos";
    }
}
