package br.edu.faculdade.academico.controller.web;
import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;
@Controller
@RequestMapping("/{tema:basico|bootstrap}/enderecos")
public class EnderecoWebController {
    private final EnderecoService endereco;
    public EnderecoWebController(EnderecoService endereco) { this.endereco=endereco; }
    @GetMapping
    public String listar(@PathVariable String tema, Model model) {
        model.addAttribute("enderecos", endereco.listar());
        return tema + "/enderecos/lista";
    }
    private String formulario(String tema, Endereco dados, Model model) {
        model.addAttribute("endereco", dados);

        return tema + "/enderecos/formulario";
    }
    @GetMapping("/novo")
    public String novo(@PathVariable String tema, Model model) { return formulario(tema, new Endereco(), model); }
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String tema, @PathVariable UUID id, Model model) {
        return formulario(tema, endereco.buscar(id), model);
    }
    @PostMapping("/salvar")
    public String salvar(@PathVariable String tema, @RequestParam(required=false) UUID id, @RequestParam String cep, @RequestParam String logradouro, @RequestParam (required=false) String numero, @RequestParam (required=false) String complemento, @RequestParam String bairro, @RequestParam String cidade, @RequestParam String uf, RedirectAttributes flash) {
        Endereco dados = new Endereco();
        dados.setCep(cep);
        dados.setLogradouro(logradouro);
        dados.setNumero(numero);
        dados.setComplemento(complemento);
        dados.setBairro(bairro);
        dados.setCidade(cidade);
        dados.setUf(uf);
        if (id == null) endereco.cadastrar(dados);
        else endereco.atualizar(id, dados);
        flash.addFlashAttribute("mensagem", "Registro salvo com sucesso!");
        return "redirect:/" + tema + "/enderecos";
    }
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String tema, @PathVariable UUID id, RedirectAttributes flash) {
        endereco.excluir(id);
        flash.addFlashAttribute("mensagem", "Registro excluído.");
        return "redirect:/" + tema + "/enderecos";
    }
}
