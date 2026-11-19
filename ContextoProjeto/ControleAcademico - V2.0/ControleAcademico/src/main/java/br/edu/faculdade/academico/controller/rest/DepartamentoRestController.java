package br.edu.faculdade.academico.controller.rest;

import br.edu.faculdade.academico.model.Departamento;
import br.edu.faculdade.academico.service.DepartamentoService;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoRestController {

    private final DepartamentoService service;

    public DepartamentoRestController(DepartamentoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public DepartamentoDTO buscar(@PathVariable UUID id) {

        Departamento departamento = service.buscar(id);

        return new DepartamentoDTO(
            departamento.getId(),
            departamento.getNome(),
            departamento.getSigla()
        );
    }

    public record DepartamentoDTO(
        UUID id,
        String nome,
        String sigla
    ) {}
}
