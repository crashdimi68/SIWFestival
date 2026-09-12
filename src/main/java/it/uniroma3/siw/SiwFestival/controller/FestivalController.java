package it.uniroma3.siw.SiwFestival.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.SiwFestival.model.Festival;
import it.uniroma3.siw.SiwFestival.model.Film;
import it.uniroma3.siw.SiwFestival.model.Proiezione;
import it.uniroma3.siw.SiwFestival.service.RisorsaNonTrovataException;
import it.uniroma3.siw.SiwFestival.service.FestivalService;
import it.uniroma3.siw.SiwFestival.service.FilmService;
import it.uniroma3.siw.SiwFestival.service.ProgrammaService;
import it.uniroma3.siw.SiwFestival.service.ProiezioneService;
import jakarta.validation.Valid;

@Controller
public class FestivalController {

    @Autowired
    private FestivalService festivalService;
    @Autowired
    private FilmService filmService;
    @Autowired
    private ProiezioneService proiezioneService;
    @Autowired
    private ProgrammaService programmaService;

    @GetMapping("/festival")
    public String list(Model model) {
        model.addAttribute("festivalList", festivalService.findAll());
        return "listFestival";
    }

    @GetMapping("/festival/{id}")
    public String show(@PathVariable Long id, Model model) {
        Festival festival = festivalService.findByIdWithFilm(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("il festival", id));
        model.addAttribute("festival", festival);
        return "showFestival";
    }

    /** Elenco semplice delle proiezioni del festival. */
    @GetMapping("/festival/{id}/proiezioni")
    public String proiezioniFestival(@PathVariable Long id, Model model) {
        Festival festival = festivalService.findById(id).orElse(null);
        if (festival == null)
            return "redirect:/festival";
        List<Proiezione> proiezioni = proiezioneService.findByFestivalId(id);
        model.addAttribute("festival", festival);
        model.addAttribute("proiezioni", proiezioni);
        return "listProiezioni";
    }

    /** Programma completo del festival, costruito dal ProgrammaService. */
    @GetMapping("/festival/{id}/programma")
    public String programma(@PathVariable Long id, Model model) {
        Festival festival = festivalService.findById(id).orElse(null);
        if (festival == null)
            return "redirect:/festival";
        model.addAttribute("festival", festival);
        model.addAttribute("programma", programmaService.programmaPerFestival(id));
        return "programma";
    }

    @GetMapping("/admin/festival/new")
    public String createForm(Model model) {
        model.addAttribute("festival", new Festival());
        return "formFestival";
    }

    @PostMapping("/admin/festival")
    public String newFestival(@Valid @ModelAttribute("festival") Festival festival,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "formFestival";
        if (festival.getDataFine() != null && festival.getDataInizio() != null
                && festival.getDataFine().isBefore(festival.getDataInizio())) {
            bindingResult.rejectValue("dataFine", "date.invalid",
                    "La data di fine non puo' precedere la data di inizio.");
            return "formFestival";
        }
        festivalService.save(festival);
        return "redirect:/festival";
    }

    @GetMapping("/admin/festival/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Festival festival = festivalService.findById(id).orElse(null);
        if (festival == null)
            return "redirect:/festival";
        model.addAttribute("festival", festival);
        return "formFestival";
    }

    @PostMapping("/admin/festival/{id}/edit")
    public String editFestival(@PathVariable Long id,
            @Valid @ModelAttribute("festival") Festival festival,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "formFestival";
        if (festival.getDataFine() != null && festival.getDataInizio() != null
                && festival.getDataFine().isBefore(festival.getDataInizio())) {
            bindingResult.rejectValue("dataFine", "date.invalid",
                    "La data di fine non puo' precedere la data di inizio.");
            return "formFestival";
        }
        festivalService.aggiorna(id, festival);
        return "redirect:/festival/" + id;
    }

    @GetMapping("/admin/festival/{id}/addFilm")
    public String addFilmForm(@PathVariable Long id, Model model) {
        Festival festival = festivalService.findByIdWithFilm(id).orElse(null);
        if (festival == null)
            return "redirect:/festival";
        List<Film> disponibili = filmService.findAll();
        if (festival.getFilm() != null) {
            disponibili.removeAll(festival.getFilm());
        }
        model.addAttribute("festival", festival);
        model.addAttribute("filmDisponibili", disponibili);
        return "addFilmAFestival";
    }

    @PostMapping("/admin/festival/{id}/addFilm")
    public String addFilm(@PathVariable Long id, @RequestParam Long filmId) {
        festivalService.associaFilm(id, filmId);
        return "redirect:/festival/" + id;
    }

    @PostMapping("/admin/festival/{festivalId}/removeFilm/{filmId}")
    public String removeFilm(@PathVariable Long festivalId, @PathVariable Long filmId) {
        festivalService.rimuoviFilm(festivalId, filmId);
        return "redirect:/festival/" + festivalId;
    }

    @PostMapping("/admin/festival/{id}/delete")
    public String deleteFestival(@PathVariable Long id) {
        festivalService.delete(id);
        return "redirect:/festival";
    }
}
