package it.uniroma3.siw.SiwFestival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.SiwFestival.model.Sala;
import it.uniroma3.siw.SiwFestival.service.ProiezioneService;
import it.uniroma3.siw.SiwFestival.service.SalaService;
import jakarta.validation.Valid;

@Controller
public class SalaController {

    @Autowired
    private SalaService salaService;

    @Autowired
    private ProiezioneService proiezioneService;

    @GetMapping("/sala/{id}")
    public String show(@PathVariable Long id, Model model) {
        Sala sala = salaService.findById(id).orElse(null);
        if (sala == null)
            return "redirect:/festival";
        model.addAttribute("sala", sala);
        model.addAttribute("proiezioni", proiezioneService.findBySalaId(id));
        return "showSala";
    }

    @GetMapping("/admin/sale")
    public String list(Model model) {
        model.addAttribute("sale", salaService.findAll());
        return "listSale";
    }

    @GetMapping("/admin/sale/new")
    public String createForm(Model model) {
        model.addAttribute("sala", new Sala());
        return "formSala";
    }

    @PostMapping("/admin/sale")
    public String newSala(@Valid @ModelAttribute("sala") Sala sala, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "formSala";
        salaService.save(sala);
        return "redirect:/admin/sale";
    }

    @GetMapping("/admin/sala/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Sala sala = salaService.findById(id).orElse(null);
        if (sala == null)
            return "redirect:/admin/sale";
        model.addAttribute("sala", sala);
        return "formSala";
    }

    @PostMapping("/admin/sala/{id}/edit")
    public String editSala(@PathVariable Long id,
            @Valid @ModelAttribute("sala") Sala sala,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "formSala";
        sala.setId(id);
        salaService.save(sala);
        return "redirect:/admin/sale";
    }
}
