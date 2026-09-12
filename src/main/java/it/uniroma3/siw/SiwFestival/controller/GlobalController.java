package it.uniroma3.siw.SiwFestival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import it.uniroma3.siw.SiwFestival.model.User;
import it.uniroma3.siw.SiwFestival.service.CredentialsService;

@ControllerAdvice
public class GlobalController {

    @Autowired
    private CredentialsService credentialsService;

    @ModelAttribute("userDetails")
    public UserDetails getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = auth.getPrincipal();
        return (principal instanceof UserDetails ud) ? ud : null;
    }

    @ModelAttribute("currentUserId")
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) return null;
        User u = credentialsService.getUserByUsername(auth.getName());
        return u != null ? u.getId() : null;
    }
}
