package br.edu.faculdade.academico.controller.web;

import br.edu.faculdade.academico.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaInicialController {
    private final DashboardService dashboardService;

    public PaginaInicialController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String inicio() { return "index"; }

    @GetMapping("/basico")
    public String basico(Model model) {
        dashboardService.preencherIndicadores(model);
        return "basico/index";
    }

    @GetMapping("/bootstrap")
    public String bootstrap(Model model) {
        dashboardService.preencherIndicadores(model);
        return "bootstrap/index";
    }
}
