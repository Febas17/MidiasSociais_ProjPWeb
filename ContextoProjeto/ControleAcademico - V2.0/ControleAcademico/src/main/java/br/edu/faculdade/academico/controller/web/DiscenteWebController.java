package br.edu.faculdade.academico.controller.web;

import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;

@Controller
@RequestMapping("/{tema:basico|bootstrap}/discentes")
public class DiscenteWebController {
    private final DiscenteService discentes;
    private final CursoService cursos;
    private final DiscenteFotoService fotos;

    public DiscenteWebController(DiscenteService discentes, CursoService cursos, DiscenteFotoService fotos) {
        this.discentes = discentes;
        this.cursos = cursos;
        this.fotos = fotos;
    }

    @GetMapping
    public String listar(@PathVariable String tema, Model model) {
        model.addAttribute("discentes", discentes.listar());
        return tema + "/discentes/lista";
    }

    private String formulario(String tema, Discente discente, Model model) {
        model.addAttribute("discente", discente);
        model.addAttribute("cursos", cursos.listar());
        return tema + "/discentes/formulario";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable String tema, Model model) {
        return formulario(tema, new Discente(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String tema, @PathVariable UUID id, Model model) {
        return formulario(tema, discentes.buscar(id), model);
    }

    @PostMapping("/salvar")
    @Transactional
    public String salvar(
            @PathVariable String tema,
            @RequestParam(required = false) UUID id,
            @RequestParam String nome,
            @RequestParam UUID cursoId,
            @RequestParam String cep,
            @RequestParam String logradouro,
            @RequestParam String numero,
            @RequestParam(required = false, defaultValue = "") String complemento,
            @RequestParam String bairro,
            @RequestParam String cidade,
            @RequestParam String uf,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            RedirectAttributes flash) {

        if (nome.isBlank() || cep.isBlank() ||
                logradouro.isBlank() || numero.isBlank() || bairro.isBlank() ||
                cidade.isBlank() || !cep.matches("\\d{5}-?\\d{3}") ||
                !uf.trim().matches("(?i)[A-Z]{2}")) {
            throw new IllegalArgumentException("Preencha corretamente os dados obrigatórios e o endereço.");
        }

        Curso curso = new Curso();
        curso.setId(cursoId);
        Endereco endereco = new Endereco();
        endereco.setCep(cep.trim());
        endereco.setLogradouro(logradouro.trim());
        endereco.setNumero(numero.trim());
        endereco.setComplemento(complemento.trim());
        endereco.setBairro(bairro.trim());
        endereco.setCidade(cidade.trim());
        endereco.setUf(uf.trim().toUpperCase(java.util.Locale.ROOT));

        Discente discente = new Discente();
        discente.setNome(nome.trim());
        discente.setCurso(curso);
        discente.setEndereco(endereco);

        fotos.validar(foto); // Antes de gravar o estudante.
        Discente salvo;
        if (id == null) {
            salvo = discentes.cadastrar(discente);
        } else {
            salvo = discentes.atualizar(id, discente);
        }
        fotos.salvar(salvo.getId(), foto); // Arquivo vazio na edição preserva a imagem.
        flash.addFlashAttribute("mensagem",
                id == null ? "Discente cadastrado com matrícula " + salvo.getMatricula() :
                        "Discente atualizado; matrícula preservada.");
        return "redirect:/" + tema + "/discentes";
    }

    @PostMapping("/{id}/excluir")
    @Transactional
    public String excluir(@PathVariable String tema,
                          @PathVariable UUID id,
                          RedirectAttributes flash) {
        fotos.excluir(id);
        discentes.excluir(id);
        flash.addFlashAttribute("mensagem", "Discente excluído.");
        return "redirect:/" + tema + "/discentes";
    }
}
