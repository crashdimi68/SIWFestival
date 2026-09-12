package it.uniroma3.siw.SiwFestival.service;

/**
 * Sollevata quando un utente prova a inserire una seconda recensione
 * per un film che ha gia' recensito.
 */
public class RecensioneDuplicataException extends RuntimeException {

    public RecensioneDuplicataException(String messaggio) {
        super(messaggio);
    }
}
