package it.uniroma3.siw.SiwFestival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.SiwFestival.model.Regista;
import it.uniroma3.siw.SiwFestival.repository.RegistaRepository;

@Service
public class RegistaService {

    @Autowired
    private RegistaRepository registaRepository;

    @Transactional(readOnly = true)
    public Optional<Regista> findById(Long id) {
        return registaRepository.findById(id);
    }

    /** Dettaglio regista: film caricati con la stessa query (EntityGraph). */
    @Transactional(readOnly = true)
    public Optional<Regista> findByIdWithFilm(Long id) {
        return registaRepository.findWithFilmById(id);
    }

    @Transactional(readOnly = true)
    public List<Regista> findAll() {
        return registaRepository.findAll();
    }

    @Transactional
    public void save(Regista regista) {
        registaRepository.save(regista);
    }

    @Transactional
    public void delete(Long id) {
        registaRepository.deleteById(id);
    }
}
