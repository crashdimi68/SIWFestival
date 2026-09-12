package it.uniroma3.siw.SiwFestival.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.SiwFestival.dto.FetchScenarioRisultato;
import it.uniroma3.siw.SiwFestival.service.FetchBenchmarkService;

@Controller
public class PerformanceController {

    @Autowired
    private FetchBenchmarkService fetchBenchmarkService;

    @GetMapping("/admin/performance")
    public String performance(Model model) {
        List<FetchScenarioRisultato> risultati = fetchBenchmarkService.eseguiConfronto();
        long maxTempoMs = risultati.stream().mapToLong(FetchScenarioRisultato::getTempoMs).max().orElse(1);

        model.addAttribute("risultati", risultati);
        model.addAttribute("numeroFilm", risultati.isEmpty() ? 0 : risultati.get(0).getNumeroFilm());
        model.addAttribute("numeroRegisti", risultati.isEmpty() ? 0 : risultati.get(0).getNumeroRegisti());
        model.addAttribute("maxTempoMs", Math.max(1, maxTempoMs));
        return "performance";
    }
}
