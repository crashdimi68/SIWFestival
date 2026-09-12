package it.uniroma3.siw.SiwFestival.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.SiwFestival.dto.FestivalDTO;
import it.uniroma3.siw.SiwFestival.dto.FilmDTO;
import it.uniroma3.siw.SiwFestival.dto.ProiezioneDTO;
import it.uniroma3.siw.SiwFestival.model.Festival;
import it.uniroma3.siw.SiwFestival.service.FestivalService;
import it.uniroma3.siw.SiwFestival.service.ProiezioneService;

/**
 * API REST sui festival, consumate dal frontend React.
 * I dati escono sempre come DTO: le entita' JPA non vengono serializzate
 * direttamente, per non trascinare associazioni LAZY e cicli.
 */
@RestController
@RequestMapping("/api/festivals")
public class FestivalRestController {

    @Autowired
    private FestivalService festivalService;

    @Autowired
    private ProiezioneService proiezioneService;

    /** GET /api/festivals */
    @GetMapping
    public ResponseEntity<List<FestivalDTO>> tutti() {
        List<FestivalDTO> festival = festivalService.findAll().stream()
                .map(FestivalDTO::from)
                .toList();
        return ResponseEntity.ok(festival);
    }

    /** GET /api/festivals/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<FestivalDTO> uno(@PathVariable Long id) {
        return festivalService.findById(id)
                .map(FestivalDTO::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** GET /api/festivals/{id}/movies */
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<FilmDTO>> filmDelFestival(@PathVariable Long id) {
        Festival festival = festivalService.findByIdWithFilm(id).orElse(null);
        if (festival == null) {
            return ResponseEntity.notFound().build();
        }
        List<FilmDTO> film = festival.getFilm().stream()
                .map(FilmDTO::from)
                .toList();
        return ResponseEntity.ok(film);
    }

    /** GET /api/festivals/{id}/screenings */
    @GetMapping("/{id}/screenings")
    public ResponseEntity<List<ProiezioneDTO>> proiezioniDelFestival(@PathVariable Long id) {
        if (festivalService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ProiezioneDTO> proiezioni = proiezioneService.findByFestivalId(id).stream()
                .map(ProiezioneDTO::from)
                .toList();
        return ResponseEntity.ok(proiezioni);
    }
}
