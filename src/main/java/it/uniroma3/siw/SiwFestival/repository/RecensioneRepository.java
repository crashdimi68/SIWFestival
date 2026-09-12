package it.uniroma3.siw.SiwFestival.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.SiwFestival.model.Recensione;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    @EntityGraph(attributePaths = { "autore" })
    List<Recensione> findByFilmIdOrderByDataDesc(Long filmId);

    List<Recensione> findByFilmId(Long filmId);

    List<Recensione> findByAutoreId(Long autoreId);

    /** Regola di business: al massimo una recensione per (utente, film). */
    boolean existsByFilmIdAndAutoreId(Long filmId, Long autoreId);

    @Query("SELECT AVG(r.voto) FROM Recensione r WHERE r.film.id = :filmId")
    Double votoMedioPerFilm(@Param("filmId") Long filmId);
}
