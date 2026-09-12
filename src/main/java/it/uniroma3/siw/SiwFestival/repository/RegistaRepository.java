package it.uniroma3.siw.SiwFestival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.uniroma3.siw.SiwFestival.model.Regista;

public interface RegistaRepository extends JpaRepository<Regista, Long> {

    List<Regista> findByCognomeIgnoreCase(String cognome);

    @EntityGraph(attributePaths = { "film" })
    Optional<Regista> findWithFilmById(Long id);

    @Query("SELECT DISTINCT r FROM Regista r LEFT JOIN FETCH r.film")
    List<Regista> findAllWithFilm();
}
