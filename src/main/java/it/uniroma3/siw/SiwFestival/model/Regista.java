package it.uniroma3.siw.SiwFestival.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

@Entity
public class Regista {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    @NotNull
    @Past
    private LocalDate dataNascita;

    @NotBlank
    private String nazionalita;

    /**
     * Un regista puo' aver diretto piu' film.
     * Associazione LAZY: la lista dei film serve solo nella pagina di dettaglio
     * del regista, non ogni volta che si carica un regista.
     */
    @OneToMany(mappedBy = "regista", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Film> film = new ArrayList<>();

    public Regista() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getNazionalita() {
        return nazionalita;
    }

    public void setNazionalita(String nazionalita) {
        this.nazionalita = nazionalita;
    }

    public List<Film> getFilm() {
        return film;
    }

    public void setFilm(List<Film> film) {
        this.film = film;
    }

    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cognome, dataNascita);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Regista other = (Regista) obj;
        return Objects.equals(nome, other.nome)
                && Objects.equals(cognome, other.cognome)
                && Objects.equals(dataNascita, other.dataNascita);
    }
}
