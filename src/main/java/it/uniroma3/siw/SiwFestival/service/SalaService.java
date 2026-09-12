package it.uniroma3.siw.SiwFestival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.SiwFestival.model.Sala;
import it.uniroma3.siw.SiwFestival.repository.SalaRepository;

@Service
public class SalaService {

    @Autowired
    private SalaRepository salaRepository;

    @Transactional(readOnly = true)
    public Optional<Sala> findById(Long id) {
        return salaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Sala> findAll() {
        return salaRepository.findAll();
    }

    @Transactional
    public void save(Sala sala) {
        salaRepository.save(sala);
    }

    @Transactional
    public void delete(Long id) {
        salaRepository.deleteById(id);
    }
}
