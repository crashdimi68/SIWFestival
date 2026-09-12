package it.uniroma3.siw.SiwFestival.dto;

import it.uniroma3.siw.SiwFestival.model.Film;

public class FilmDTO {

    private Long id;
    private String titolo;
    private Integer anno;
    private Integer durata;
    private String genere;
    private String paeseProduzione;
    private String regista;

    public FilmDTO() {
    }

    public static FilmDTO from(Film f) {
        FilmDTO dto = new FilmDTO();
        dto.id = f.getId();
        dto.titolo = f.getTitolo();
        dto.anno = f.getAnno();
        dto.durata = f.getDurata();
        dto.genere = f.getGenere() != null ? f.getGenere().getLabel() : null;
        dto.paeseProduzione = f.getPaeseProduzione();
        dto.regista = f.getRegista() != null ? f.getRegista().getNomeCompleto() : null;
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }
    public Integer getDurata() { return durata; }
    public void setDurata(Integer durata) { this.durata = durata; }
    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }
    public String getPaeseProduzione() { return paeseProduzione; }
    public void setPaeseProduzione(String paeseProduzione) { this.paeseProduzione = paeseProduzione; }
    public String getRegista() { return regista; }
    public void setRegista(String regista) { this.regista = regista; }
}
