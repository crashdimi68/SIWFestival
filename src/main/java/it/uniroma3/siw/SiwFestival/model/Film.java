package it.uniroma3.siw.SiwFestival.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String titolo;

    @NotNull
    @Min(1888)
    @Max(2100)
    private Integer anno;

    /** Durata in minuti: serve anche per calcolare l'orario di fine proiezione. */
    @NotNull
    @Min(1)
    @Max(600)
    private Integer durata;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GenereFilm genere;

    @NotBlank
    private String paeseProduzione;

    /**
     * Ogni film ha un regista.
     * LAZY di proposito: e' l'associazione su cui si osserva il problema N+1
     * nella pagina di analisi delle prestazioni.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regista_id")
    @JsonBackReference
    private Regista regista;

    /** Un film puo' partecipare a uno o piu' festival (lato NON proprietario). */
    @ManyToMany(mappedBy = "film", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Festival> festival = new ArrayList<>();

    /** Un film puo' avere piu' proiezioni. */
    @OneToMany(mappedBy = "film", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Proiezione> proiezioni = new ArrayList<>();

    /** Un film puo' avere piu' recensioni. */
    @OneToMany(mappedBy = "film", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Recensione> recensioni = new ArrayList<>();

    public Film() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public Integer getDurata() {
        return durata;
    }

    public void setDurata(Integer durata) {
        this.durata = durata;
    }

    public GenereFilm getGenere() {
        return genere;
    }

    public void setGenere(GenereFilm genere) {
        this.genere = genere;
    }

    public String getPaeseProduzione() {
        return paeseProduzione;
    }

    public void setPaeseProduzione(String paeseProduzione) {
        this.paeseProduzione = paeseProduzione;
    }

    public Regista getRegista() {
        return regista;
    }

    public void setRegista(Regista regista) {
        this.regista = regista;
    }

    public List<Festival> getFestival() {
        return festival;
    }

    public void setFestival(List<Festival> festival) {
        this.festival = festival;
    }

    public List<Proiezione> getProiezioni() {
        return proiezioni;
    }

    public void setProiezioni(List<Proiezione> proiezioni) {
        this.proiezioni = proiezioni;
    }

    public List<Recensione> getRecensioni() {
        return recensioni;
    }

    public void setRecensioni(List<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    @Override
    public int hashCode() {
        return Objects.hash(titolo, anno);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Film other = (Film) obj;
        return Objects.equals(titolo, other.titolo)
                && Objects.equals(anno, other.anno);
    }
}
