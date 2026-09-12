package it.uniroma3.siw.festivalcinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festivalcinema.service.FestivalService;
import it.uniroma3.siw.festivalcinema.util.Pagination;

@Controller
public class HomeController {

    private final FestivalService festivalService;

    public HomeController(FestivalService festivalService) {
        this.festivalService = festivalService;
    }

    @GetMapping({ "/", "/index" })
    public String getHome(@RequestParam(defaultValue = "0") Integer page, Model model) {
        model.addAttribute("festivals", festivalService.findInProgress(Pagination.of(page)));
        return "index";
    }
}
