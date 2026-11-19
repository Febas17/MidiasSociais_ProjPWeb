package br.edu.faculdade.academico.controller.web;
import br.edu.faculdade.academico.model.*;
import br.edu.faculdade.academico.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.List;
@Controller
@RequestMapping("/{tema:basico|bootstrap}/imagens")
public class ImagemWebController {
    private final ImagemService imagens;
    private final DiscenteService discentes;
    public ImagemWebController(ImagemService imagens, DiscenteService discentes) { this.imagens=imagens; this.discentes=discentes; }
    @GetMapping public String listar(@PathVariable String tema, Model model) {
        model.addAttribute("imagens",imagens.listar()); return tema+"/imagens/lista";
    }
    @GetMapping("/novo") public String novo(@PathVariable String tema,Model model) {
        model.addAttribute("discentes", discentes.listar()); return tema+"/imagens/formulario";
    }
    @PostMapping("/salvar") public String salvar(@PathVariable String tema, @RequestParam UUID discenteId,
        @RequestParam("arquivo") MultipartFile arquivo, RedirectAttributes flash) throws IOException {
        if (arquivo.isEmpty() || arquivo.getSize()>2_000_000) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Imagem vazia ou acima de 2MB");
        String tipo=arquivo.getContentType();
        if (!List.of("image/jpeg","image/png","image/webp").contains(tipo)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Use JPEG, PNG ou WebP");
        Imagem dados=new Imagem(); Discente discente=new Discente(); discente.setId(discenteId);
        dados.setDiscente(discente); dados.setBytes(arquivo.getBytes()); dados.setTipoMime(tipo);
        dados.setNomeArquivo(arquivo.getOriginalFilename()); imagens.cadastrar(dados);
        flash.addFlashAttribute("mensagem","Imagem enviada."); return "redirect:/"+tema+"/imagens";
    }
    @GetMapping("/{id}/arquivo") @ResponseBody
    public ResponseEntity<byte[]> arquivo(@PathVariable UUID id) {
        Imagem img=imagens.buscar(id);
        String mime=img.getTipoMime();
        if (!List.of("image/jpeg","image/png","image/webp").contains(mime)) throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mime))
            .header("X-Content-Type-Options","nosniff")
            .body(img.getBytes());
    }
    @PostMapping("/{id}/excluir") public String excluir(@PathVariable String tema,@PathVariable UUID id,RedirectAttributes flash) {
        imagens.excluir(id);flash.addFlashAttribute("mensagem","Imagem excluída.");return "redirect:/"+tema+"/imagens";
    }
}
