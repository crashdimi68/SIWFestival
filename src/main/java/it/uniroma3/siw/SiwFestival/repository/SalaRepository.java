package it.uniroma3.siw.SiwFestival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.SiwFestival.model.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    Optional<Sala> findByNome(String nome);

    List<Sala> findByCapienzaGreaterThanEqual(int capienza);
}
