package it.uniroma3.siw.SiwFestival.service;

/**
 * Sollevata quando la programmazione di una proiezione violerebbe un vincolo di
 * consistenza: sala gia' occupata, film non iscritto al festival, data fuori
 * dall'intervallo del festival.
 */
public class ProiezioneNonValidaException extends RuntimeException {

    public ProiezioneNonValidaException(String messaggio) {
        super(messaggio);
    }
}
