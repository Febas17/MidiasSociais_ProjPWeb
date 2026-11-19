package br.edu.faculdade.academico.controller.web;

import br.edu.faculdade.academico.model.Departamento;
import br.edu.faculdade.academico.model.Professor;
import br.edu.faculdade.academico.service.ProfessorFotoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.UUID;

/** Substitui o controller antigo de professores das interfaces /basico e /bootstrap. */
@Controller
@RequestMapping({"/basico/professores", "/bootstrap/professores"})
public class ProfessorWebController {
    @PersistenceContext private EntityManager em;
    private final ProfessorFotoService fotos;
    public ProfessorWebController(ProfessorFotoService fotos) { this.fotos = fotos; }

    private String tema(String url) { return url.startsWith("/bootstrap/") ? "bootstrap" : "basico"; }

    @GetMapping
    @Transactional(readOnly = true)
    public String listar(jakarta.servlet.http.HttpServletRequest request, Model model) {
        model.addAttribute("professores", em.createQuery(
            "select p from Professor p join fetch p.departamento order by p.nome", Professor.class)
            .getResultList());
        return tema(request.getRequestURI()) + "/professores/lista";
    }

    @GetMapping("/novo")
    @Transactional(readOnly = true)
    public String novo(jakarta.servlet.http.HttpServletRequest request, Model model) {
        model.addAttribute("professor", new Professor());
        model.addAttribute("departamentos", em.createQuery(
            "select d from Departamento d order by d.nome", Departamento.class).getResultList());
        return tema(request.getRequestURI()) + "/professores/formulario";
    }

    @GetMapping("/{id}/editar")
    @Transactional(readOnly = true)
    public String editar(@PathVariable UUID id, jakarta.servlet.http.HttpServletRequest request, Model model) {
        Professor p = em.find(Professor.class, id);
        if (p == null) throw new IllegalArgumentException("Professor não encontrado.");
        p.getDepartamento().getId();
        model.addAttribute("professor", p);
        model.addAttribute("departamentos", em.createQuery(
            "select d from Departamento d order by d.nome", Departamento.class).getResultList());
        return tema(request.getRequestURI()) + "/professores/formulario";
    }

    @PostMapping(value="/salvar", consumes="multipart/form-data")
    @Transactional(rollbackFor = Exception.class)
    public String salvar(@RequestParam(required=false) UUID id,
                         @RequestParam String registro, @RequestParam String nome,
                         @RequestParam(required=false) String email,
                         @RequestParam UUID departamentoId,
                         @RequestParam(required=false) MultipartFile foto,
                         jakarta.servlet.http.HttpServletRequest request,
                         RedirectAttributes mensagens) throws IOException {
        boolean novo = id == null;
        Professor p = novo ? new Professor() : em.find(Professor.class, id);
        if (p == null) throw new IllegalArgumentException("Professor não encontrado.");
        Departamento dep = em.find(Departamento.class, departamentoId);
        if (dep == null) throw new IllegalArgumentException("Departamento não encontrado.");
        if (registro.isBlank() || nome.isBlank())
            throw new IllegalArgumentException("Registro e nome são obrigatórios.");
        p.setRegistro(registro.trim());
        p.setNome(nome.trim());
        p.setEmail(email == null || email.isBlank() ? null : email.trim());
        p.setDepartamento(dep);
        if (novo) em.persist(p);
        em.flush();
        fotos.salvar(p.getId(), foto);
        mensagens.addFlashAttribute("mensagem", "Professor salvo com sucesso.");
        return "redirect:/" + tema(request.getRequestURI()) + "/professores";
    }

    @PostMapping("/{id}/excluir")
    @Transactional
    public String excluir(@PathVariable UUID id, jakarta.servlet.http.HttpServletRequest request) {
        Professor p = em.find(Professor.class, id);
        if (p != null) {
            fotos.excluir(id);
            em.remove(p);
        }
        return "redirect:/" + tema(request.getRequestURI()) + "/professores";
    }
}
