package br.edu.faculdade.academico.controller.web;
import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;
@Controller
@RequestMapping("/{tema:basico|bootstrap}/disciplinas")
public class DisciplinaWebController {
    private final DisciplinaService disciplina;
    private final CursoService cursos;
    public DisciplinaWebController(DisciplinaService disciplina, CursoService cursos) { this.disciplina=disciplina; this.cursos=cursos; }
    @GetMapping
    public String listar(@PathVariable String tema, Model model) {
        model.addAttribute("disciplinas", disciplina.listar());
        return tema + "/disciplinas/lista";
    }
    private String formulario(String tema, Disciplina dados, Model model) {
        model.addAttribute("disciplina", dados);
        model.addAttribute("cursos", cursos.listar());
        return tema + "/disciplinas/formulario";
    }
    @GetMapping("/novo")
    public String novo(@PathVariable String tema, Model model) { return formulario(tema, new Disciplina(), model); }
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String tema, @PathVariable UUID id, Model model) {
        return formulario(tema, disciplina.buscar(id), model);
    }
    @PostMapping("/salvar")
    public String salvar(@PathVariable String tema, @RequestParam(required=false) UUID id, @RequestParam String codigo, @RequestParam String nome, @RequestParam Integer cargaHoraria, @RequestParam UUID cursoId, RedirectAttributes flash) {
        Disciplina dados = new Disciplina();
        dados.setCodigo(codigo);
        dados.setNome(nome);
        dados.setCargaHoraria(cargaHoraria);
        Curso curso = new Curso(); curso.setId(cursoId); dados.setCurso(curso);
        if (id == null) disciplina.cadastrar(dados);
        else disciplina.atualizar(id, dados);
        flash.addFlashAttribute("mensagem", "Registro salvo com sucesso!");
        return "redirect:/" + tema + "/disciplinas";
    }
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String tema, @PathVariable UUID id, RedirectAttributes flash) {
        disciplina.excluir(id);
        flash.addFlashAttribute("mensagem", "Registro excluído.");
        return "redirect:/" + tema + "/disciplinas";
    }
}
