package br.edu.faculdade.academico.controller.web;
import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;
@Controller
@RequestMapping("/{tema:basico|bootstrap}/turmas")
public class TurmaWebController {
    private final TurmaService turma;
    private final DisciplinaService disciplinas;
    private final ProfessorService professores;
    public TurmaWebController(TurmaService turma, DisciplinaService disciplinas, ProfessorService professores) { this.turma=turma; this.disciplinas=disciplinas; this.professores=professores; }
    @GetMapping
    public String listar(@PathVariable String tema, Model model) {
        model.addAttribute("turmas", turma.listar());
        return tema + "/turmas/lista";
    }
    private String formulario(String tema, Turma dados, Model model) {
        model.addAttribute("turma", dados);
        model.addAttribute("disciplinas", disciplinas.listar());
        model.addAttribute("professores", professores.listar());
        return tema + "/turmas/formulario";
    }
    @GetMapping("/novo")
    public String novo(@PathVariable String tema, Model model) { return formulario(tema, new Turma(), model); }
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String tema, @PathVariable UUID id, Model model) {
        return formulario(tema, turma.buscar(id), model);
    }
    @PostMapping("/salvar")
    public String salvar(@PathVariable String tema, @RequestParam(required=false) UUID id, @RequestParam String codigo, @RequestParam Integer ano, @RequestParam Integer semestre, @RequestParam UUID disciplinaId, @RequestParam UUID professorId, RedirectAttributes flash) {
        Turma dados = new Turma();
        dados.setCodigo(codigo);
        dados.setAno(ano);
        dados.setSemestre(semestre);
        Disciplina disciplina = new Disciplina(); disciplina.setId(disciplinaId); dados.setDisciplina(disciplina);
        Professor professor = new Professor(); professor.setId(professorId); dados.setProfessor(professor);
        if (id == null) turma.cadastrar(dados);
        else turma.atualizar(id, dados);
        flash.addFlashAttribute("mensagem", "Registro salvo com sucesso!");
        return "redirect:/" + tema + "/turmas";
    }
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String tema, @PathVariable UUID id, RedirectAttributes flash) {
        turma.excluir(id);
        flash.addFlashAttribute("mensagem", "Registro excluído.");
        return "redirect:/" + tema + "/turmas";
    }
}
