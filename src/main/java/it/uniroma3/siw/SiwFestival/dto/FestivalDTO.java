package it.uniroma3.siw.SiwFestival.dto;

import java.time.LocalDate;

import it.uniroma3.siw.SiwFestival.model.Festival;

public class FestivalDTO {

    private Long id;
    private String nome;
    private Integer anno;
    private String citta;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String descrizione;

    public FestivalDTO() {
    }

    public static FestivalDTO from(Festival f) {
        FestivalDTO dto = new FestivalDTO();
        dto.id = f.getId();
        dto.nome = f.getNome();
        dto.anno = f.getAnno();
        dto.citta = f.getCitta();
        dto.dataInizio = f.getDataInizio();
        dto.dataFine = f.getDataFine();
        dto.descrizione = f.getDescrizione();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }
    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }
    public LocalDate getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDate dataInizio) { this.dataInizio = dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public void setDataFine(LocalDate dataFine) { this.dataFine = dataFine; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}
