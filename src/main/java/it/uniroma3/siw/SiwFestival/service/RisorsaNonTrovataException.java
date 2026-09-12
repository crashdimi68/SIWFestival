package it.uniroma3.siw.SiwFestival.service;

/**
 * Sollevata dal Service Layer quando una risorsa applicativa richiesta non esiste
 * (es. /film/999 con un id che non e' nel database).
 * Non e' un errore di routing: e' un caso d'uso, quindi viene gestito nel service
 * e tradotto in una pagina 404 dal GlobalExceptionHandler.
 */
public class RisorsaNonTrovataException extends RuntimeException {

    public RisorsaNonTrovataException(String tipo, Long id) {
        super("Non esiste " + tipo + " con id " + id + ".");
    }

    public RisorsaNonTrovataException(String messaggio) {
        super(messaggio);
    }
}
