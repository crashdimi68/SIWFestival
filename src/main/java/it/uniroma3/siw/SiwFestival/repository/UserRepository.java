package it.uniroma3.siw.SiwFestival.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.SiwFestival.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
