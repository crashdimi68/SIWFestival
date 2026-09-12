package it.uniroma3.siw.SiwFestival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.SiwFestival.model.Film;
import it.uniroma3.siw.SiwFestival.model.GenereFilm;
import it.uniroma3.siw.SiwFestival.model.Regista;
import it.uniroma3.siw.SiwFestival.service.RisorsaNonTrovataException;
import it.uniroma3.siw.SiwFestival.service.FestivalService;
import it.uniroma3.siw.SiwFestival.service.FilmService;
import it.uniroma3.siw.SiwFestival.service.ProiezioneService;
import it.uniroma3.siw.SiwFestival.service.RecensioneService;
import it.uniroma3.siw.SiwFestival.service.RegistaService;
import jakarta.validation.Valid;

@Controller
public class FilmController {

    /** Quanti film vengono mostrati in una pagina dell'elenco. */
    private static final int FILM_PER_PAGINA = 6;

    @Autowired
    private FilmService filmService;
    @Autowired
    private RegistaService registaService;
    @Autowired
    private FestivalService festivalService;
    @Autowired
    private ProiezioneService proiezioneService;
    @Autowired
    private RecensioneService recensioneService;

    /**
     * Elenco dei film, sei per pagina. Il numero di pagina arriva come parametro
     * di richiesta (/film?page=2); se manca si intende la prima. Una richiesta
     * oltre l'ultima pagina viene riportata sull'ultima esistente con un redirect,
     * cosi' l'utente non si ritrova davanti un elenco vuoto.
     */
    @GetMapping("/film")
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Film> pagina = filmService.findPagina(Math.max(page, 0), FILM_PER_PAGINA);
        if (pagina.getTotalPages() > 0 && page >= pagina.getTotalPages()) {
            return "redirect:/film?page=" + (pagina.getTotalPages() - 1);
        }
        model.addAttribute("film", pagina.getContent());
        model.addAttribute("pagina", pagina);
        return "listFilm";
    }

    /** Pagina che ospita il widget React di ricerca dei film. */
    @GetMapping("/film/cerca")
    public String cerca(Model model) {
        return "cercaFilm";
    }

    @GetMapping("/film/{id}")
    public String show(@PathVariable Long id, Model model) {
        Film film = filmService.findByIdWithRecensioni(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("il film", id));
        model.addAttribute("film", film);
        model.addAttribute("festivalPartecipati", festivalService.findByFilmId(id));
        model.addAttribute("proiezioni", proiezioneService.findByFilmId(id));
        model.addAttribute("recensioni", recensioneService.findByFilmId(id));
        model.addAttribute("votoMedio", recensioneService.votoMedio(id));
        return "showFilm";
    }

    @GetMapping("/admin/film/new")
    public String createForm(@RequestParam(required = false) Long festivalId, Model model) {
        model.addAttribute("filmForm", new Film());
        model.addAttribute("registi", registaService.findAll());
        model.addAttribute("generi", GenereFilm.values());
        model.addAttribute("festivalId", festivalId);
        return "formFilm";
    }

    @PostMapping("/admin/film")
    public String newFilm(@Valid @ModelAttribute("filmForm") Film filmForm,
            BindingResult bindingResult,
            @RequestParam(required = false) Long registaId,
            @RequestParam(required = false) Long festivalId,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registi", registaService.findAll());
            model.addAttribute("generi", GenereFilm.values());
            model.addAttribute("festivalId", festivalId);
            return "formFilm";
        }
        Regista regista = registaId != null ? registaService.findById(registaId).orElse(null) : null;
        filmForm.setRegista(regista);
        Film salvato = filmService.save(filmForm);

        if (festivalId != null) {
            return "redirect:/admin/festival/" + festivalId + "/addFilm";
        }
        return "redirect:/film/" + salvato.getId();
    }

    @GetMapping("/admin/film/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Film film = filmService.findById(id).orElse(null);
        if (film == null)
            return "redirect:/film";
        model.addAttribute("filmForm", film);
        model.addAttribute("registi", registaService.findAll());
        model.addAttribute("generi", GenereFilm.values());
        return "formFilm";
    }

    @PostMapping("/admin/film/{id}/edit")
    public String editFilm(@PathVariable Long id,
            @Valid @ModelAttribute("filmForm") Film filmForm,
            BindingResult bindingResult,
            @RequestParam(required = false) Long registaId,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registi", registaService.findAll());
            model.addAttribute("generi", GenereFilm.values());
            return "formFilm";
        }
        Regista regista = registaId != null ? registaService.findById(registaId).orElse(null) : null;
        filmService.aggiorna(id, filmForm, regista);
        return "redirect:/film/" + id;
    }

    @PostMapping("/admin/film/{id}/delete")
    public String deleteFilm(@PathVariable Long id) {
        filmService.delete(id);
        return "redirect:/film";
    }
}
