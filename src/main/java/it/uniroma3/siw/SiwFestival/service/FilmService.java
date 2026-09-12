package it.uniroma3.siw.SiwFestival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.SiwFestival.model.Festival;
import it.uniroma3.siw.SiwFestival.model.Film;
import it.uniroma3.siw.SiwFestival.model.GenereFilm;
import it.uniroma3.siw.SiwFestival.model.Regista;
import it.uniroma3.siw.SiwFestival.repository.FestivalRepository;
import it.uniroma3.siw.SiwFestival.repository.FilmRepository;
import it.uniroma3.siw.SiwFestival.repository.ProiezioneRepository;

@Service
public class FilmService {

    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private FestivalRepository festivalRepository;
    @Autowired
    private ProiezioneRepository proiezioneRepository;

    @Transactional(readOnly = true)
    public Optional<Film> findById(Long id) {
        return filmRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Film> findByIdWithRecensioni(Long id) {
        return filmRepository.findWithRecensioniById(id);
    }

    @Transactional(readOnly = true)
    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    /** Elenco film con il regista caricato in un'unica query. */
    @Transactional(readOnly = true)
    public List<Film> findAllWithRegista() {
        return filmRepository.findAllWithRegista();
    }

    /**
     * Una singola pagina dell'elenco dei film, ordinata per titolo.
     * La paginazione avviene nel database: la lista completa dei film non viene
     * mai caricata in memoria. L'oggetto Page restituito porta con se' anche il
     * numero totale di pagine e di elementi, che servono alla vista per disegnare
     * la barra di navigazione.
     */
    @Transactional(readOnly = true)
    public Page<Film> findPagina(int numeroPagina, int filmPerPagina) {
        return filmRepository.findAllBy(
                PageRequest.of(numeroPagina, filmPerPagina, Sort.by("titolo").ascending()));
    }

    @Transactional(readOnly = true)
    public List<Film> cercaPerTitolo(String titolo) {
        return filmRepository.cercaPerTitoloConRegista(titolo);
    }

    @Transactional(readOnly = true)
    public List<Film> findByGenere(GenereFilm genere) {
        return filmRepository.findByGenere(genere);
    }

    @Transactional
    public Film save(Film film) {
        return filmRepository.save(film);
    }

    /**
     * Aggiornamento di un film gia' esistente.
     * Non si fa merge dell'oggetto che arriva dal form: quello ha le collezioni vuote
     * e, dato che Film.recensioni e' mappata con orphanRemoval, un merge diretto
     * cancellerebbe tutte le recensioni del film. Si carica invece l'entita' gestita
     * e si copiano solo i campi modificabili dal form.
     */
    @Transactional
    public Film aggiorna(Long id, Film dati, Regista regista) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Film non trovato: " + id));
        film.setTitolo(dati.getTitolo());
        film.setAnno(dati.getAnno());
        film.setDurata(dati.getDurata());
        film.setGenere(dati.getGenere());
        film.setPaeseProduzione(dati.getPaeseProduzione());
        film.setRegista(regista);
        return filmRepository.save(film);
    }

    /**
     * Eliminazione di un film: prima vanno tolte le proiezioni che lo riguardano e
     * va sciolta l'associazione con i festival ai quali partecipa, altrimenti la
     * cancellazione violerebbe i vincoli di integrita' referenziale.
     * Le recensioni seguono il film per cascata.
     */
    @Transactional
    public void delete(Long id) {
        proiezioneRepository.deleteAll(proiezioneRepository.findByFilmId(id));

        for (Festival festival : festivalRepository.findByFilm_Id(id)) {
            festival.getFilm().removeIf(f -> f.getId().equals(id));
            festivalRepository.save(festival);
        }

        filmRepository.deleteById(id);
    }
}
