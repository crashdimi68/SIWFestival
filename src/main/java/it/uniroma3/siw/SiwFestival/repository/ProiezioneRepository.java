package it.uniroma3.siw.SiwFestival.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.SiwFestival.model.Proiezione;
import it.uniroma3.siw.SiwFestival.model.StatoProiezione;

public interface ProiezioneRepository extends JpaRepository<Proiezione, Long> {

    List<Proiezione> findByFestivalIdOrderByDataAscOraAsc(Long festivalId);

    List<Proiezione> findByFilmId(Long filmId);

    List<Proiezione> findBySalaId(Long salaId);

    /**
     * Proiezioni gia' programmate in una sala in un dato giorno, escluse quelle annullate.
     * Il film viene caricato insieme perche' serve la durata per il controllo di sovrapposizione.
     */
    @EntityGraph(attributePaths = { "film" })
    List<Proiezione> findBySalaIdAndDataAndStatoNot(Long salaId, LocalDate data, StatoProiezione stato);

    /** Programma del festival: film e sala caricati in un'unica query. */
    @EntityGraph(attributePaths = { "film", "sala" })
    @Query("SELECT p FROM Proiezione p WHERE p.festival.id = :festivalId ORDER BY p.data ASC, p.ora ASC")
    List<Proiezione> findByFestivalIdWithFilmESala(@Param("festivalId") Long festivalId);

    /** Stessa query ma senza fetch: usata per mostrare il costo delle associazioni LAZY. */
    @Query("SELECT p FROM Proiezione p WHERE p.festival.id = :festivalId ORDER BY p.data ASC, p.ora ASC")
    List<Proiezione> findByFestivalIdLazy(@Param("festivalId") Long festivalId);
}
