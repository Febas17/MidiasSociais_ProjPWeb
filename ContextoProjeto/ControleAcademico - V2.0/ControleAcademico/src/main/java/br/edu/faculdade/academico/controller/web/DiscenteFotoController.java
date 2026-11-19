package br.edu.faculdade.academico.controller.web;

import br.edu.faculdade.academico.model.Imagem;
import br.edu.faculdade.academico.service.DiscenteFotoService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
public class DiscenteFotoController {
    private final DiscenteFotoService fotos;
    public DiscenteFotoController(DiscenteFotoService fotos) { this.fotos = fotos; }

    @GetMapping("/discentes/{id}/foto")
    public ResponseEntity<byte[]> imagem(@PathVariable UUID id) {
        Imagem foto = fotos.buscar(id);
        if (foto == null || foto.getBytes() == null || foto.getBytes().length == 0) {
            return ResponseEntity.notFound().build();
        }
        String mime = foto.getTipoMime();
        MediaType tipo = "image/jpeg".equals(mime) ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG;
        return ResponseEntity.ok().contentType(tipo).cacheControl(CacheControl.noCache())
            .body(foto.getBytes());
    }
}
