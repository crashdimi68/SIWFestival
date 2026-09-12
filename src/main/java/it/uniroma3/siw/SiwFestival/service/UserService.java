package it.uniroma3.siw.SiwFestival.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.SiwFestival.model.User;
import it.uniroma3.siw.SiwFestival.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
