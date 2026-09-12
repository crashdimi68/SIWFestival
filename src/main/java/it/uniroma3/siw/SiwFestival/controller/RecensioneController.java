package it.uniroma3.siw.SiwFestival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.SiwFestival.model.User;
import it.uniroma3.siw.SiwFestival.service.CredentialsService;
import it.uniroma3.siw.SiwFestival.service.RecensioneDuplicataException;
import it.uniroma3.siw.SiwFestival.service.RecensioneService;

@Controller
public class RecensioneController {

    @Autowired
    private RecensioneService recensioneService;
    @Autowired
    private CredentialsService credentialsService;

    @PostMapping("/film/{filmId}/recensione")
    public String addRecensione(@PathVariable Long filmId,
            @RequestParam String testo,
            @RequestParam Integer voto,
            RedirectAttributes redirectAttrs) {
        try {
            recensioneService.inserisci(filmId, utenteCorrente(), testo, voto);
        } catch (RecensioneDuplicataException e) {
            redirectAttrs.addFlashAttribute("errore", e.getMessage());
        } catch (AccessDeniedException e) {
            redirectAttrs.addFlashAttribute("errore", "Devi essere autenticato per recensire un film.");
        }
        return "redirect:/film/" + filmId;
    }

    @PostMapping("/film/{filmId}/recensione/{recensioneId}/edit")
    public String editRecensione(@PathVariable Long filmId,
            @PathVariable Long recensioneId,
            @RequestParam String testo,
            @RequestParam(required = false) Integer voto,
            RedirectAttributes redirectAttrs) {
        try {
            recensioneService.modifica(recensioneId, testo, voto, utenteCorrente());
        } catch (AccessDeniedException e) {
            redirectAttrs.addFlashAttribute("errore", "Puoi modificare solo le tue recensioni.");
        }
        return "redirect:/film/" + filmId;
    }

    @PostMapping("/film/{filmId}/recensione/{recensioneId}/delete")
    public String deleteRecensione(@PathVariable Long filmId,
            @PathVariable Long recensioneId,
            RedirectAttributes redirectAttrs) {
        try {
            recensioneService.elimina(recensioneId, utenteCorrente(), isAdmin());
        } catch (AccessDeniedException e) {
            redirectAttrs.addFlashAttribute("errore", "Puoi eliminare solo le tue recensioni.");
        }
        return "redirect:/film/" + filmId;
    }

    private User utenteCorrente() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return credentialsService.getUserByUsername(username);
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
