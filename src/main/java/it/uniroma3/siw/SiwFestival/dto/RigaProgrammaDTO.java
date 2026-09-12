package it.uniroma3.siw.SiwFestival.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import it.uniroma3.siw.SiwFestival.model.Proiezione;

/**
 * Una riga del programma di un festival: dati gia' pronti per la vista,
 * compreso l'orario di fine calcolato sulla durata del film.
 */
public class RigaProgrammaDTO {

    private int posizione;
    private Long proiezioneId;
    private LocalDate data;
    private LocalTime ora;
    private LocalTime oraFine;
    private Long filmId;
    private String filmTitolo;
    private Integer filmDurata;
    private String filmGenere;
    private Long salaId;
    private String salaNome;
    private int salaCapienza;
    private String stato;
    private boolean primaDelGiorno;

    public RigaProgrammaDTO() {
    }

    public static RigaProgrammaDTO forProiezione(Proiezione p) {
        RigaProgrammaDTO r = new RigaProgrammaDTO();
        r.proiezioneId = p.getId();
        r.data = p.getData();
        r.ora = p.getOra();
        r.stato = p.getStato() != null ? p.getStato().name() : null;
        if (p.getFilm() != null) {
            r.filmId = p.getFilm().getId();
            r.filmTitolo = p.getFilm().getTitolo();
            r.filmDurata = p.getFilm().getDurata();
            r.filmGenere = p.getFilm().getGenere() != null ? p.getFilm().getGenere().getLabel() : null;
        }
        if (p.getSala() != null) {
            r.salaId = p.getSala().getId();
            r.salaNome = p.getSala().getNome();
            r.salaCapienza = p.getSala().getCapienza();
        }
        if (r.ora != null) {
            int minuti = r.filmDurata != null ? r.filmDurata : 120;
            r.oraFine = r.ora.plusMinutes(minuti);
        }
        return r;
    }

    public int getPosizione() { return posizione; }
    public void setPosizione(int posizione) { this.posizione = posizione; }
    public Long getProiezioneId() { return proiezioneId; }
    public void setProiezioneId(Long proiezioneId) { this.proiezioneId = proiezioneId; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getOra() { return ora; }
    public void setOra(LocalTime ora) { this.ora = ora; }
    public LocalTime getOraFine() { return oraFine; }
    public void setOraFine(LocalTime oraFine) { this.oraFine = oraFine; }
    public Long getFilmId() { return filmId; }
    public void setFilmId(Long filmId) { this.filmId = filmId; }
    public String getFilmTitolo() { return filmTitolo; }
    public void setFilmTitolo(String filmTitolo) { this.filmTitolo = filmTitolo; }
    public Integer getFilmDurata() { return filmDurata; }
    public void setFilmDurata(Integer filmDurata) { this.filmDurata = filmDurata; }
    public String getFilmGenere() { return filmGenere; }
    public void setFilmGenere(String filmGenere) { this.filmGenere = filmGenere; }
    public Long getSalaId() { return salaId; }
    public void setSalaId(Long salaId) { this.salaId = salaId; }
    public String getSalaNome() { return salaNome; }
    public void setSalaNome(String salaNome) { this.salaNome = salaNome; }
    public int getSalaCapienza() { return salaCapienza; }
    public void setSalaCapienza(int salaCapienza) { this.salaCapienza = salaCapienza; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    public boolean isPrimaDelGiorno() { return primaDelGiorno; }
    public void setPrimaDelGiorno(boolean primaDelGiorno) { this.primaDelGiorno = primaDelGiorno; }
}
