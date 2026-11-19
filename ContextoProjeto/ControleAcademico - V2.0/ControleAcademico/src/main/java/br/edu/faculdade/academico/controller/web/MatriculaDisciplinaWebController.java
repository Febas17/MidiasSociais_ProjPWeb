package br.edu.faculdade.academico.controller.web;
import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;
@Controller
@RequestMapping("/{tema:basico|bootstrap}/matriculas")
public class MatriculaDisciplinaWebController {
    private final MatriculaDisciplinaService matriculaDisciplina;
    private final DiscenteService discentes;
    private final TurmaService turmas;
    public MatriculaDisciplinaWebController(MatriculaDisciplinaService matriculaDisciplina, DiscenteService discentes, TurmaService turmas) { this.matriculaDisciplina=matriculaDisciplina; this.discentes=discentes; this.turmas=turmas; }
    @GetMapping
    public String listar(@PathVariable String tema, Model model) {
        model.addAttribute("matriculas", matriculaDisciplina.listar());
        return tema + "/matriculas/lista";
    }
    private String formulario(String tema, MatriculaDisciplina dados, Model model) {
        model.addAttribute("matriculaDisciplina", dados);
        model.addAttribute("discentes", discentes.listar());
        model.addAttribute("turmas", turmas.listar());
        return tema + "/matriculas/formulario";
    }
    @GetMapping("/novo")
    public String novo(@PathVariable String tema, Model model) { return formulario(tema, new MatriculaDisciplina(), model); }
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String tema, @PathVariable UUID id, Model model) {
        return formulario(tema, matriculaDisciplina.buscar(id), model);
    }
    @PostMapping("/salvar")
    public String salvar(@PathVariable String tema, @RequestParam(required=false) UUID id, @RequestParam (required=false) java.math.BigDecimal notaFinal, @RequestParam SituacaoMatricula situacao, @RequestParam UUID discenteId, @RequestParam UUID turmaId, RedirectAttributes flash) {
        MatriculaDisciplina dados = new MatriculaDisciplina();
        dados.setNotaFinal(notaFinal);
        dados.setSituacao(situacao);
        Discente discente = new Discente(); discente.setId(discenteId); dados.setDiscente(discente);
        Turma turma = new Turma(); turma.setId(turmaId); dados.setTurma(turma);
        if (id == null) matriculaDisciplina.cadastrar(dados);
        else matriculaDisciplina.atualizar(id, dados);
        flash.addFlashAttribute("mensagem", "Registro salvo com sucesso!");
        return "redirect:/" + tema + "/matriculas";
    }
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String tema, @PathVariable UUID id, RedirectAttributes flash) {
        matriculaDisciplina.excluir(id);
        flash.addFlashAttribute("mensagem", "Registro excluído.");
        return "redirect:/" + tema + "/matriculas";
    }
}
