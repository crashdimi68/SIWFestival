package it.uniroma3.siw.SiwFestival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.SiwFestival.model.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    List<Festival> findByNome(String nome);

    List<Festival> findByAnno(int anno);

    List<Festival> findByCittaIgnoreCase(String citta);

    @Query("SELECT f FROM Festival f WHERE f.anno >= :annoLimite AND f.nome LIKE %:parolaChiave%")
    List<Festival> ricercaComplessaFestival(@Param("annoLimite") int anno,
            @Param("parolaChiave") String parola);

    @Query("SELECT f FROM Festival f WHERE f.id = :id")
    Optional<Festival> findByIdLazy(@Param("id") Long id);

    @Query("SELECT DISTINCT f FROM Festival f LEFT JOIN FETCH f.film WHERE f.id = :id")
    Optional<Festival> findByIdJoinFetch(@Param("id") Long id);

    @EntityGraph(attributePaths = { "film" })
    @Query("SELECT f FROM Festival f WHERE f.id = :id")
    Optional<Festival> findByIdWithEntityGraph(@Param("id") Long id);

    /** Tutti i festival ai quali partecipa un certo film. */
    @EntityGraph(attributePaths = { "film" })
    List<Festival> findByFilm_Id(Long filmId);
}
