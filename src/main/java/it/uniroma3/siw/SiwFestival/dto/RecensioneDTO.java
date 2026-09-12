package it.uniroma3.siw.SiwFestival.dto;

import java.time.LocalDateTime;

import it.uniroma3.siw.SiwFestival.model.Recensione;

public class RecensioneDTO {

    private Long id;
    private String testo;
    private Integer voto;
    private LocalDateTime data;
    private String autore;

    public RecensioneDTO() {
    }

    public static RecensioneDTO from(Recensione r) {
        RecensioneDTO dto = new RecensioneDTO();
        dto.id = r.getId();
        dto.testo = r.getTesto();
        dto.voto = r.getVoto();
        dto.data = r.getData();
        dto.autore = r.getAutore() != null ? r.getAutore().getName() : "Anonimo";
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }
    public Integer getVoto() { return voto; }
    public void setVoto(Integer voto) { this.voto = voto; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }
}
