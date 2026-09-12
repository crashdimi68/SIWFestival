package it.uniroma3.siw.SiwFestival.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Una recensione riguarda un film ed e' scritta da un utente registrato.
 * Il vincolo di unicita' (film, autore) implementa a livello di database la regola
 * "un utente puo' inserire al massimo una recensione per uno stesso film";
 * il controllo applicativo sta in RecensioneService.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_recensione_film_autore",
        columnNames = { "film_id", "autore_id" }))
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 1000)
    @Column(length = 1000)
    private String testo;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer voto;

    @Column(nullable = false)
    private LocalDateTime data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autore_id")
    private User autore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id")
    private Film film;

    public Recensione() {
    }

    @PrePersist
    protected void onCreate() {
        if (data == null) {
            data = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Integer getVoto() {
        return voto;
    }

    public void setVoto(Integer voto) {
        this.voto = voto;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public User getAutore() {
        return autore;
    }

    public void setAutore(User autore) {
        this.autore = autore;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recensione other = (Recensione) obj;
        return Objects.equals(id, other.id);
    }
}
