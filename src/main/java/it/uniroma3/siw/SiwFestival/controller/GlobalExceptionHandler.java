package it.uniroma3.siw.SiwFestival.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import it.uniroma3.siw.SiwFestival.service.ProiezioneNonValidaException;
import it.uniroma3.siw.SiwFestival.service.RecensioneDuplicataException;
import it.uniroma3.siw.SiwFestival.service.RisorsaNonTrovataException;

/**
 * Gestore centralizzato degli errori.
 *
 * I controller non contengono logica di gestione degli errori inattesi: il service
 * solleva un'eccezione, questa classe la intercetta e decide quale vista mostrare
 * e quale codice di stato HTTP restituire. Questo separa le responsabilita', evita
 * try/catch sparsi nei controller e centralizza la manutenzione.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404 applicativo: l'URL e' mappato, ma la risorsa richiesta non esiste. */
    @ExceptionHandler(RisorsaNonTrovataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleRisorsaNonTrovata(RisorsaNonTrovataException e, Model model) {
        logger.warn("Risorsa non trovata: {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "error/404";
    }

    /** 404 di routing: non esiste nessun controller mappato su quell'URL. */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException e, Model model) {
        model.addAttribute("errorMessage", "La pagina richiesta non esiste.");
        return "error/404";
    }

    /** Violazione di una regola di business sulla programmazione delle proiezioni. */
    @ExceptionHandler(ProiezioneNonValidaException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleProiezioneNonValida(ProiezioneNonValidaException e, Model model) {
        logger.warn("Programmazione non valida: {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "error/errore";
    }

    /** Un utente ha provato a recensire due volte lo stesso film. */
    @ExceptionHandler(RecensioneDuplicataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleRecensioneDuplicata(RecensioneDuplicataException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/errore";
    }

    /** L'utente e' autenticato ma non ha i permessi per l'operazione richiesta. */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        model.addAttribute("errorMessage", "Non hai i permessi per eseguire questa operazione.");
        return "error/errore";
    }

    /** Qualsiasi altro errore inatteso: l'utente vede una pagina comprensibile, noi lo stack trace nei log. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpectedException(Exception e, Model model) {
        logger.error("Errore interno non gestito", e);
        model.addAttribute("errorMessage",
                "Si e' verificato un errore interno. Riprovare piu' tardi.");
        return "error/500";
    }
}
