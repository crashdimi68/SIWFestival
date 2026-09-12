package it.uniroma3.siw.SiwFestival.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Regola globale di binding: elimina gli spazi all'inizio e alla fine di ogni
 * campo di tipo String raccolto dalle form. Senza questa regola " Dune " verrebbe
 * salvato nel database con gli spazi.
 *
 * Il parametro true di StringTrimmerEditor trasforma le stringhe vuote in null.
 */
@ControllerAdvice
public class GlobalBindingConfig {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
