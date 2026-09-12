package it.uniroma3.siw.SiwFestival.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

@Entity
public class Proiezione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private LocalDate data;

    @NotNull
    private LocalTime ora;

    @Enumerated(EnumType.STRING)
    private StatoProiezione stato;

    /** Una proiezione appartiene a un festival. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    private Festival festival;

    /** Una proiezione riguarda un film. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id")
    private Film film;

    /** Una proiezione si svolge in una sala. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id")
    private Sala sala;

    public Proiezione() {
    }

    /** Comodo per ordinamenti e confronti: data e ora insieme. */
    @Transient
    public LocalDateTime getDataEOra() {
        if (data == null || ora == null) {
            return null;
        }
        return LocalDateTime.of(data, ora);
    }

    /**
     * Orario di fine calcolato sulla durata del film.
     * Se il film non e' caricato o non ha durata si assume una durata di 120 minuti.
     */
    @Transient
    public LocalDateTime getFineStimata() {
        LocalDateTime inizio = getDataEOra();
        if (inizio == null) {
            return null;
        }
        int minuti = (film != null && film.getDurata() != null) ? film.getDurata() : 120;
        return inizio.plusMinutes(minuti);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOra() {
        return ora;
    }

    public void setOra(LocalTime ora) {
        this.ora = ora;
    }

    public StatoProiezione getStato() {
        return stato;
    }

    public void setStato(StatoProiezione stato) {
        this.stato = stato;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, ora);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Proiezione other = (Proiezione) obj;
        return Objects.equals(data, other.data)
                && Objects.equals(ora, other.ora);
    }
}
