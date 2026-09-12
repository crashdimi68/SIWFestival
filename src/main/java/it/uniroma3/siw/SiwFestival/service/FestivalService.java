package it.uniroma3.siw.SiwFestival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.SiwFestival.model.Festival;
import it.uniroma3.siw.SiwFestival.model.Film;
import it.uniroma3.siw.SiwFestival.repository.FestivalRepository;
import it.uniroma3.siw.SiwFestival.repository.FilmRepository;
import it.uniroma3.siw.SiwFestival.repository.ProiezioneRepository;

@Service
public class FestivalService {

    @Autowired
    private FestivalRepository festivalRepository;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private ProiezioneRepository proiezioneRepository;

    @Transactional(readOnly = true)
    public Optional<Festival> findById(Long id) {
        return festivalRepository.findById(id);
    }

    /** Dettaglio festival: i film partecipanti arrivano con la stessa query. */
    @Transactional(readOnly = true)
    public Optional<Festival> findByIdWithFilm(Long id) {
        return festivalRepository.findByIdWithEntityGraph(id);
    }

    @Transactional(readOnly = true)
    public List<Festival> findAll() {
        return festivalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Festival> findByNome(String nome) {
        return festivalRepository.findByNome(nome);
    }

    @Transactional(readOnly = true)
    public List<Festival> findByFilmId(Long filmId) {
        return festivalRepository.findByFilm_Id(filmId);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Festival save(Festival festival) {
        return festivalRepository.save(festival);
    }

    /**
     * Aggiornamento di un festival esistente.
     * Come per il film, non si fa merge dell'oggetto del form: Festival.film e' il lato
     * proprietario della molti-a-molti e un merge con la lista vuota svuoterebbe la
     * tabella di associazione festival_film.
     */
    @Transactional
    public Festival aggiorna(Long id, Festival dati) {
        Festival festival = festivalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Festival non trovato: " + id));
        festival.setNome(dati.getNome());
        festival.setAnno(dati.getAnno());
        festival.setCitta(dati.getCitta());
        festival.setDataInizio(dati.getDataInizio());
        festival.setDataFine(dati.getDataFine());
        festival.setDescrizione(dati.getDescrizione());
        return festivalRepository.save(festival);
    }

    /**
     * Associazione di un film a un festival.
     * Coinvolge due entita' e va eseguita in un'unica transazione: se il salvataggio
     * fallisce, il film non deve risultare parzialmente iscritto.
     */
    @Transactional
    public void associaFilm(Long festivalId, Long filmId) {
        Festival festival = festivalRepository.findByIdWithEntityGraph(festivalId)
                .orElseThrow(() -> new IllegalArgumentException("Festival non trovato: " + festivalId));
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film non trovato: " + filmId));

        if (!festival.getFilm().contains(film)) {
            festival.getFilm().add(film);
            festivalRepository.save(festival);
        }
    }

    /**
     * Rimozione di un film dal festival.
     * Vanno eliminate anche le proiezioni di quel film in quel festival, altrimenti
     * resterebbero proiezioni di un film che non partecipa piu' alla rassegna.
     */
    @Transactional
    public void rimuoviFilm(Long festivalId, Long filmId) {
        Festival festival = festivalRepository.findByIdWithEntityGraph(festivalId)
                .orElseThrow(() -> new IllegalArgumentException("Festival non trovato: " + festivalId));

        proiezioneRepository.deleteAll(
                proiezioneRepository.findByFestivalIdOrderByDataAscOraAsc(festivalId).stream()
                        .filter(p -> p.getFilm() != null && p.getFilm().getId().equals(filmId))
                        .toList());

        festival.getFilm().removeIf(f -> f.getId().equals(filmId));
        festivalRepository.save(festival);
    }

    @Transactional
    public void delete(Long id) {
        proiezioneRepository.deleteAll(proiezioneRepository.findByFestivalIdOrderByDataAscOraAsc(id));
        festivalRepository.deleteById(id);
    }
}
