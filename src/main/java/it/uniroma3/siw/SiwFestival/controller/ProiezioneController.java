package it.uniroma3.siw.SiwFestival.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.SiwFestival.model.Proiezione;
import it.uniroma3.siw.SiwFestival.model.StatoProiezione;
import it.uniroma3.siw.SiwFestival.service.FestivalService;
import it.uniroma3.siw.SiwFestival.service.ProiezioneNonValidaException;
import it.uniroma3.siw.SiwFestival.service.ProiezioneService;
import it.uniroma3.siw.SiwFestival.service.SalaService;

@Controller
public class ProiezioneController {

    @Autowired
    private ProiezioneService proiezioneService;
    @Autowired
    private FestivalService festivalService;
    @Autowired
    private SalaService salaService;

    @GetMapping("/proiezione/{id}")
    public String show(@PathVariable Long id, Model model) {
        Proiezione proiezione = proiezioneService.findById(id).orElse(null);
        if (proiezione == null)
            return "redirect:/festival";
        model.addAttribute("proiezione", proiezione);
        return "showProiezione";
    }

    @GetMapping("/admin/proiezioni")
    public String listAll(Model model) {
        model.addAttribute("proiezioni", proiezioneService.findAll());
        return "listAllProiezioni";
    }

    @GetMapping("/admin/proiezioni/new")
    public String createForm(@RequestParam(required = false) Long festivalId, Model model) {
        model.addAttribute("festivalList", festivalService.findAll());
        model.addAttribute("sale", salaService.findAll());
        model.addAttribute("festivalSelezionato", festivalId);
        if (festivalId != null) {
            festivalService.findByIdWithFilm(festivalId)
                    .ifPresent(f -> model.addAttribute("filmDisponibili", f.getFilm()));
        }
        return "formProiezione";
    }

    /**
     * Programmazione di una nuova proiezione: il controller si limita a validare il
     * formato dell'input e a delegare il caso d'uso al Service Layer, che lo esegue
     * in un'unica transazione. Gli errori di consistenza tornano come messaggio.
     */
    @PostMapping("/admin/proiezioni")
    public String newProiezione(@RequestParam Long festivalId,
            @RequestParam Long filmId,
            @RequestParam Long salaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime ora,
            Model model) {
        try {
            proiezioneService.programmaProiezione(festivalId, filmId, salaId, data, ora);
        } catch (ProiezioneNonValidaException e) {
            model.addAttribute("errore", e.getMessage());
            model.addAttribute("festivalList", festivalService.findAll());
            model.addAttribute("sale", salaService.findAll());
            model.addAttribute("festivalSelezionato", festivalId);
            festivalService.findByIdWithFilm(festivalId)
                    .ifPresent(f -> model.addAttribute("filmDisponibili", f.getFilm()));
            return "formProiezione";
        }
        return "redirect:/festival/" + festivalId + "/programma";
    }

    @GetMapping("/admin/proiezione/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Proiezione proiezione = proiezioneService.findById(id).orElse(null);
        if (proiezione == null)
            return "redirect:/festival";
        model.addAttribute("proiezione", proiezione);
        model.addAttribute("sale", salaService.findAll());
        model.addAttribute("stati", StatoProiezione.values());
        return "formModificaProiezione";
    }

    @PostMapping("/admin/proiezione/{id}/edit")
    public String editProiezione(@PathVariable Long id,
            @RequestParam Long salaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime ora,
            Model model) {
        try {
            proiezioneService.riprogramma(id, salaId, data, ora);
        } catch (ProiezioneNonValidaException e) {
            Proiezione proiezione = proiezioneService.findById(id).orElse(null);
            model.addAttribute("proiezione", proiezione);
            model.addAttribute("sale", salaService.findAll());
            model.addAttribute("stati", StatoProiezione.values());
            model.addAttribute("errore", e.getMessage());
            return "formModificaProiezione";
        }
        return "redirect:/proiezione/" + id;
    }

    @PostMapping("/admin/proiezione/{id}/stato")
    public String cambiaStato(@PathVariable Long id,
            @RequestParam StatoProiezione stato,
            RedirectAttributes redirectAttrs) {
        try {
            proiezioneService.cambiaStato(id, stato);
        } catch (ProiezioneNonValidaException e) {
            redirectAttrs.addFlashAttribute("errore", e.getMessage());
        }
        return "redirect:/proiezione/" + id;
    }

    @PostMapping("/admin/proiezione/{id}/delete")
    public String deleteProiezione(@PathVariable Long id) {
        Proiezione proiezione = proiezioneService.findById(id).orElse(null);
        Long festivalId = (proiezione != null && proiezione.getFestival() != null)
                ? proiezione.getFestival().getId()
                : null;
        if (proiezione != null) {
            proiezioneService.delete(proiezione);
        }
        if (festivalId != null) {
            return "redirect:/festival/" + festivalId + "/programma";
        }
        return "redirect:/festival";
    }
}
