package br.edu.faculdade.academico.service;

import br.edu.faculdade.academico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class DashboardService {
    private final DepartamentoRepository departamentos;
    private final CursoRepository cursos;
    private final DiscenteRepository discentes;
    private final ProfessorRepository professores;
    private final DisciplinaRepository disciplinas;
    private final EnderecoRepository enderecos;
    private final ImagemRepository imagens;
    private final TurmaRepository turmas;
    private final MatriculaDisciplinaRepository matriculas;

    public DashboardService(DepartamentoRepository departamentos, CursoRepository cursos,
                            DiscenteRepository discentes, ProfessorRepository professores,
                            DisciplinaRepository disciplinas, EnderecoRepository enderecos,
                            ImagemRepository imagens, TurmaRepository turmas,
                            MatriculaDisciplinaRepository matriculas) {
        this.departamentos = departamentos;
        this.cursos = cursos;
        this.discentes = discentes;
        this.professores = professores;
        this.disciplinas = disciplinas;
        this.enderecos = enderecos;
        this.imagens = imagens;
        this.turmas = turmas;
        this.matriculas = matriculas;
    }

    @Transactional(readOnly = true)
    public void preencherIndicadores(Model model) {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        int ano = hoje.getYear();
        int semestre = hoje.getMonthValue() <= 6 ? 1 : 2;
        model.addAttribute("anoAtual", ano);
        model.addAttribute("semestreAtual", semestre);
        model.addAttribute("totalDepartamentos", departamentos.count());
        model.addAttribute("totalCursos", cursos.count());
        model.addAttribute("totalDiscentes", discentes.count());
        model.addAttribute("totalProfessores", professores.count());
        model.addAttribute("totalDisciplinas", disciplinas.count());
        model.addAttribute("totalEnderecos", enderecos.count());
        model.addAttribute("totalImagens", imagens.count());
        model.addAttribute("totalTurmasSemestre", turmas.countByAnoAndSemestre(ano, semestre));
        model.addAttribute("totalMatriculasSemestre", matriculas.countByTurma_AnoAndTurma_Semestre(ano, semestre));
    }
}
