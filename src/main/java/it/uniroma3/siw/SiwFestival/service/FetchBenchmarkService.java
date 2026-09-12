package it.uniroma3.siw.SiwFestival.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import it.uniroma3.siw.SiwFestival.dto.FetchScenarioRisultato;
import it.uniroma3.siw.SiwFestival.model.Film;
import it.uniroma3.siw.SiwFestival.repository.FilmRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Analisi sperimentale dell'accesso ai dati (Sezione 8.2 della specifica).
 *
 * Lo stesso caso d'uso — "carica tutti i film con il relativo regista" — viene
 * eseguito con tre strategie diverse sullo stesso insieme di dati, misurando per
 * ciascuna il numero di query SQL effettivamente eseguite (statistiche di Hibernate),
 * il tempo totale e il numero di oggetti caricati.
 */
@Service
public class FetchBenchmarkService {

    @Autowired
    private FilmRepository filmRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<FetchScenarioRisultato> eseguiConfronto() {
        Statistics statistics = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class)
                .getStatistics();

        List<FetchScenarioRisultato> risultati = new ArrayList<>();

        risultati.add(eseguiScenario(statistics,
                "Default (LAZY)",
                "findAll() e poi, per ogni film, getRegista().getCognome(): Film.regista e' LAZY, "
                        + "quindi ogni accesso al regista scatena una SELECT separata "
                        + "(1 query per l'elenco + N per i registi, il problema N+1).",
                () -> {
                    List<Film> film = filmRepository.findAll();
                    return conta(film);
                }));

        risultati.add(eseguiScenario(statistics,
                "JOIN FETCH esplicito",
                "findAllWithRegista(): JPQL scritta a mano "
                        + "'SELECT f FROM Film f LEFT JOIN FETCH f.regista', un'unica query. "
                        + "Il regista arriva insieme al film, gia' inizializzato.",
                () -> {
                    List<Film> film = filmRepository.findAllWithRegista();
                    return conta(film);
                }));

        risultati.add(eseguiScenario(statistics,
                "@EntityGraph",
                "findAllBy() con @EntityGraph(attributePaths = \"regista\"): stesso risultato del "
                        + "JOIN FETCH, ma dichiarativo, senza scrivere JPQL a mano.",
                () -> {
                    List<Film> film = filmRepository.findAllBy();
                    return conta(film);
                }));

        return risultati;
    }

    /**
     * Tocca il regista di ogni film: e' questo accesso che, con l'associazione LAZY
     * non inizializzata, provoca le query aggiuntive.
     */
    private int[] conta(List<Film> film) {
        Set<Long> registi = new HashSet<>();
        for (Film f : film) {
            if (f.getRegista() != null) {
                f.getRegista().getCognome();
                registi.add(f.getRegista().getId());
            }
        }
        return new int[] { film.size(), registi.size() };
    }

    private FetchScenarioRisultato eseguiScenario(Statistics statistics, String titolo, String descrizione,
            Supplier<int[]> scenario) {
        entityManager.clear();
        statistics.clear();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int[] conteggi = scenario.get();
        stopWatch.stop();

        long numeroQuery = statistics.getPrepareStatementCount();
        return new FetchScenarioRisultato(titolo, descrizione, numeroQuery, stopWatch.getTotalTimeMillis(),
                conteggi[0], conteggi[1]);
    }
}
