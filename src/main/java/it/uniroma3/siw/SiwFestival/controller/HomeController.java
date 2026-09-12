package it.uniroma3.siw.SiwFestival.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        return "adminDashboard";
    }
}
