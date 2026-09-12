package it.uniroma3.siw.SiwFestival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.SiwFestival.model.Credentials;
import it.uniroma3.siw.SiwFestival.model.User;
import it.uniroma3.siw.SiwFestival.service.CredentialsService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/profilo")
    public String profilo(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Credentials credentials = credentialsService.getCredentials(username).orElse(null);
        if (credentials == null) {
            return "redirect:/";
        }
        model.addAttribute("credentials", credentials);
        return "profilo";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        Credentials credentials = new Credentials();
        User user = new User();
        credentials.setUser(user);

        model.addAttribute("credentials", credentials);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("credentials") Credentials credentials,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (credentialsService.getCredentials(credentials.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "duplicate.username",
                    "Username gia' in uso, sceglierne un altro.");
            return "register";
        }

        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentialsService.saveCredentials(credentials);

        return "redirect:/login?registered=true";
    }
}
