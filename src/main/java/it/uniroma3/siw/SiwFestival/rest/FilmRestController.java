package it.uniroma3.siw.SiwFestival.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.SiwFestival.dto.FilmDTO;
import it.uniroma3.siw.SiwFestival.dto.RecensioneDTO;
import it.uniroma3.siw.SiwFestival.service.FilmService;
import it.uniroma3.siw.SiwFestival.service.RecensioneService;

/**
 * API REST sui film. E' l'endpoint usato dal widget React di ricerca.
 */
@RestController
@RequestMapping("/api/movies")
public class FilmRestController {

    @Autowired
    private FilmService filmService;

    @Autowired
    private RecensioneService recensioneService;

    /**
     * GET /api/movies
     * GET /api/movies?titolo=... per la ricerca per titolo.
     */
    @GetMapping
    public ResponseEntity<List<FilmDTO>> tutti(
            @RequestParam(required = false) String titolo) {
        List<FilmDTO> film = (titolo == null || titolo.isBlank()
                ? filmService.findAllWithRegista()
                : filmService.cercaPerTitolo(titolo))
                .stream()
                .map(FilmDTO::from)
                .toList();
        return ResponseEntity.ok(film);
    }

    /** GET /api/movies/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<FilmDTO> uno(@PathVariable Long id) {
        return filmService.findByIdWithRecensioni(id)
                .map(FilmDTO::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** GET /api/movies/{id}/reviews */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<RecensioneDTO>> recensioniDelFilm(@PathVariable Long id) {
        if (filmService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<RecensioneDTO> recensioni = recensioneService.findByFilmId(id).stream()
                .map(RecensioneDTO::from)
                .toList();
        return ResponseEntity.ok(recensioni);
    }
}
