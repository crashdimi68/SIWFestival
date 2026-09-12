package it.uniroma3.siw.SiwFestival.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.SiwFestival.model.Film;
import it.uniroma3.siw.SiwFestival.model.Recensione;
import it.uniroma3.siw.SiwFestival.model.User;
import it.uniroma3.siw.SiwFestival.repository.FilmRepository;
import it.uniroma3.siw.SiwFestival.repository.RecensioneRepository;

@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;
    @Autowired
    private FilmRepository filmRepository;

    @Transactional(readOnly = true)
    public Optional<Recensione> findById(Long id) {
        return recensioneRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Recensione> findByFilmId(Long filmId) {
        return recensioneRepository.findByFilmIdOrderByDataDesc(filmId);
    }

    @Transactional(readOnly = true)
    public Double votoMedio(Long filmId) {
        return recensioneRepository.votoMedioPerFilm(filmId);
    }

    /**
     * Inserimento di una recensione.
     * Regola di business della specifica: un utente puo' inserire al massimo una
     * recensione per uno stesso film. Il controllo e' qui, nel Service Layer, ed e'
     * rinforzato dal vincolo di unicita' (film_id, autore_id) sulla tabella.
     */
    @Transactional
    public Recensione inserisci(Long filmId, User autore, String testo, Integer voto) {
        if (autore == null) {
            throw new AccessDeniedException("Occorre essere autenticati per recensire un film.");
        }
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film non trovato: " + filmId));

        if (recensioneRepository.existsByFilmIdAndAutoreId(filmId, autore.getId())) {
            throw new RecensioneDuplicataException(
                    "Hai gia' recensito \"" + film.getTitolo() + "\": puoi modificare la tua recensione.");
        }

        Recensione recensione = new Recensione();
        recensione.setFilm(film);
        recensione.setAutore(autore);
        recensione.setTesto(testo);
        recensione.setVoto(voto);
        return recensioneRepository.save(recensione);
    }

    /** Un utente puo' modificare esclusivamente le recensioni di cui e' autore. */
    @Transactional
    public Recensione modifica(Long recensioneId, String nuovoTesto, Integer nuovoVoto,
            User utenteRichiedente) {
        Recensione recensione = recensioneRepository.findById(recensioneId)
                .orElseThrow(() -> new IllegalArgumentException("Recensione non trovata: " + recensioneId));

        if (utenteRichiedente == null
                || recensione.getAutore() == null
                || !Objects.equals(recensione.getAutore().getId(), utenteRichiedente.getId())) {
            throw new AccessDeniedException("Non sei l'autore della recensione");
        }

        recensione.setTesto(nuovoTesto);
        if (nuovoVoto != null) {
            recensione.setVoto(nuovoVoto);
        }
        return recensioneRepository.save(recensione);
    }

    /** L'autore puo' cancellare la propria recensione; l'amministratore qualunque recensione. */
    @Transactional
    public void elimina(Long recensioneId, User utenteRichiedente, boolean isAdmin) {
        Recensione recensione = recensioneRepository.findById(recensioneId)
                .orElseThrow(() -> new IllegalArgumentException("Recensione non trovata: " + recensioneId));

        boolean autore = utenteRichiedente != null
                && recensione.getAutore() != null
                && Objects.equals(recensione.getAutore().getId(), utenteRichiedente.getId());

        if (!isAdmin && !autore) {
            throw new AccessDeniedException("Non sei l'autore della recensione");
        }
        recensioneRepository.delete(recensione);
    }
}
