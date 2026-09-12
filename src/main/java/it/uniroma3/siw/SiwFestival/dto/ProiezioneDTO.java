package it.uniroma3.siw.SiwFestival.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import it.uniroma3.siw.SiwFestival.model.Proiezione;

public class ProiezioneDTO {

    private Long id;
    private LocalDate data;
    private LocalTime ora;
    private String stato;
    private Long filmId;
    private String filmTitolo;
    private String salaNome;

    public ProiezioneDTO() {
    }

    public static ProiezioneDTO from(Proiezione p) {
        ProiezioneDTO dto = new ProiezioneDTO();
        dto.id = p.getId();
        dto.data = p.getData();
        dto.ora = p.getOra();
        dto.stato = p.getStato() != null ? p.getStato().name() : null;
        if (p.getFilm() != null) {
            dto.filmId = p.getFilm().getId();
            dto.filmTitolo = p.getFilm().getTitolo();
        }
        if (p.getSala() != null) {
            dto.salaNome = p.getSala().getNome();
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getOra() { return ora; }
    public void setOra(LocalTime ora) { this.ora = ora; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    public Long getFilmId() { return filmId; }
    public void setFilmId(Long filmId) { this.filmId = filmId; }
    public String getFilmTitolo() { return filmTitolo; }
    public void setFilmTitolo(String filmTitolo) { this.filmTitolo = filmTitolo; }
    public String getSalaNome() { return salaNome; }
    public void setSalaNome(String salaNome) { this.salaNome = salaNome; }
}
