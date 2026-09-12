package it.uniroma3.siw.SiwFestival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.SiwFestival.model.Film;
import it.uniroma3.siw.SiwFestival.model.GenereFilm;

public interface FilmRepository extends JpaRepository<Film, Long> {

    List<Film> findByTitoloContainingIgnoreCase(String titolo);

    /** Ricerca per titolo con il regista gia' caricato: evita una query per ogni film trovato. */
    @Query("SELECT f FROM Film f LEFT JOIN FETCH f.regista "
            + "WHERE LOWER(f.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))")
    List<Film> cercaPerTitoloConRegista(@Param("titolo") String titolo);

    List<Film> findByGenere(GenereFilm genere);

    List<Film> findByRegistaId(Long registaId);

    /** Dettaglio film: recensioni caricate insieme al film. */
    @EntityGraph(attributePaths = { "recensioni", "regista" })
    Optional<Film> findWithRecensioniById(Long id);

    /**
     * Scenario 2 del confronto sulle strategie di fetch:
     * una sola query, JPQL scritta a mano con JOIN FETCH.
     */
    @Query("SELECT f FROM Film f LEFT JOIN FETCH f.regista")
    List<Film> findAllWithRegista();

    /**
     * Scenario 3 del confronto: stesso effetto del JOIN FETCH ma dichiarativo.
     */
    @EntityGraph(attributePaths = { "regista" })
    List<Film> findAllBy();

    /**
     * Elenco film paginato, con il regista gia' caricato.
     * Spring Data traduce il Pageable in LIMIT e OFFSET: dal database arrivano
     * soltanto i film della pagina richiesta, non l'intera tabella. Una seconda
     * query conta le righe totali, e serve a sapere quante pagine esistono.
     */
    @EntityGraph(attributePaths = { "regista" })
    Page<Film> findAllBy(Pageable pageable);
}
