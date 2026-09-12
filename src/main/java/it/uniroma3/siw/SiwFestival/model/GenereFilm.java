package it.uniroma3.siw.SiwFestival.model;

public enum GenereFilm {

    DRAMMATICO("Drammatico"),
    COMMEDIA("Commedia"),
    THRILLER("Thriller"),
    FANTASCIENZA("Fantascienza"),
    HORROR("Horror"),
    DOCUMENTARIO("Documentario"),
    ANIMAZIONE("Animazione");

    private final String label;

    GenereFilm(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
