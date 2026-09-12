package it.uniroma3.siw.SiwFestival.dto;

public class FetchScenarioRisultato {

    private final String titolo;
    private final String descrizione;
    private final long numeroQuery;
    private final long tempoMs;
    private final int numeroFilm;
    private final int numeroRegisti;

    public FetchScenarioRisultato(String titolo, String descrizione, long numeroQuery, long tempoMs,
            int numeroFilm, int numeroRegisti) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.numeroQuery = numeroQuery;
        this.tempoMs = tempoMs;
        this.numeroFilm = numeroFilm;
        this.numeroRegisti = numeroRegisti;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public long getNumeroQuery() {
        return numeroQuery;
    }

    public long getTempoMs() {
        return tempoMs;
    }

    public int getNumeroFilm() {
        return numeroFilm;
    }

    public int getNumeroRegisti() {
        return numeroRegisti;
    }
}
