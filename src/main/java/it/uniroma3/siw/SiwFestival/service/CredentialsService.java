package it.uniroma3.siw.SiwFestival.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.SiwFestival.model.Credentials;
import it.uniroma3.siw.SiwFestival.model.User;
import it.uniroma3.siw.SiwFestival.repository.CredentialsRepository;

@Service
public class CredentialsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Transactional(readOnly = true)
    public Optional<Credentials> getCredentials(Long id) {
        return credentialsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Credentials> getCredentials(String username) {
        return credentialsRepository.findByUsername(username);
    }

    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        return credentialsRepository.save(credentials);
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        Optional<Credentials> credentials = credentialsRepository.findByUsername(username);
        return credentials.map(Credentials::getUser).orElse(null);
    }
}
