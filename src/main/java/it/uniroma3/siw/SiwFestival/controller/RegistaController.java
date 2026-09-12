package it.uniroma3.siw.SiwFestival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.SiwFestival.model.Regista;
import it.uniroma3.siw.SiwFestival.service.RisorsaNonTrovataException;
import it.uniroma3.siw.SiwFestival.service.RegistaService;
import jakarta.validation.Valid;

@Controller
public class RegistaController {

    @Autowired
    private RegistaService registaService;

    @GetMapping("/registi")
    public String list(Model model) {
        model.addAttribute("registi", registaService.findAll());
        return "listRegisti";
    }

    @GetMapping("/regista/{id}")
    public String show(@PathVariable Long id, Model model) {
        Regista regista = registaService.findByIdWithFilm(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("il regista", id));
        model.addAttribute("regista", regista);
        return "showRegista";
    }

    @GetMapping("/admin/registi/new")
    public String createForm(Model model) {
        model.addAttribute("regista", new Regista());
        return "formRegista";
    }

    @PostMapping("/admin/registi")
    public String newRegista(@Valid @ModelAttribute("regista") Regista regista,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "formRegista";
        registaService.save(regista);
        return "redirect:/registi";
    }

    @GetMapping("/admin/regista/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Regista regista = registaService.findById(id).orElse(null);
        if (regista == null)
            return "redirect:/registi";
        model.addAttribute("regista", regista);
        return "formRegista";
    }

    @PostMapping("/admin/regista/{id}/edit")
    public String editRegista(@PathVariable Long id,
            @Valid @ModelAttribute("regista") Regista regista,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "formRegista";
        regista.setId(id);
        registaService.save(regista);
        return "redirect:/regista/" + id;
    }

    @PostMapping("/admin/regista/{id}/delete")
    public String deleteRegista(@PathVariable Long id) {
        registaService.delete(id);
        return "redirect:/registi";
    }
}
