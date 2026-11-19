package br.edu.faculdade.academico.controller.web;

import br.edu.faculdade.academico.model.Imagem;
import br.edu.faculdade.academico.service.ProfessorFotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ProfessorFotoController {
    private final ProfessorFotoService fotos;
    public ProfessorFotoController(ProfessorFotoService fotos) { this.fotos = fotos; }

    @GetMapping("/professores/{id}/foto")
    public ResponseEntity<byte[]> exibir(@PathVariable UUID id) {
        Imagem img = fotos.buscar(id);
        if (img == null || img.getBytes() == null || img.getBytes().length == 0)
            return ResponseEntity.notFound().build();
        MediaType tipo = "image/jpeg".equals(img.getTipoMime())
                ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG;
        return ResponseEntity.ok().contentType(tipo).body(img.getBytes());
    }
}
