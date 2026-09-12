package it.uniroma3.siw.festivalcinema.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festivalcinema.model.Director;
import it.uniroma3.siw.festivalcinema.service.DirectorService;
import it.uniroma3.siw.festivalcinema.service.MovieService;
import it.uniroma3.siw.festivalcinema.util.Pagination;
import jakarta.validation.Valid;

@Controller
public class DirectorController {

    private final DirectorService directorService;
    private final MovieService movieService;

    public DirectorController(DirectorService directorService, MovieService movieService) {
        this.directorService = directorService;
        this.movieService = movieService;
    }

    @GetMapping("/directors")
    public String list(@RequestParam(defaultValue = "0") Integer page, Model model) {
        model.addAttribute("directors", directorService.findAll(Pagination.of(page)));
        return "directors/list";
    }

    @GetMapping("/directors/{id}")
    public String show(@PathVariable Long id, @RequestParam(defaultValue = "0") Integer page, Model model) {
        Optional<Director> optional = directorService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/directors";
        }
        model.addAttribute("director", optional.get());
        // filmografia paginata invece della collezione director.movies
        model.addAttribute("movies", movieService.findByDirectorId(id, Pagination.of(page)));
        return "directors/show";
    }

    @GetMapping("/admin/directors/new")
    public String createForm(Model model) {
        model.addAttribute("director", new Director());
        return "admin/directors/form";
    }

    @GetMapping("/admin/directors/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Director> optional = directorService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/directors";
        }
        model.addAttribute("director", optional.get());
        return "admin/directors/form";
    }

    @PostMapping("/admin/directors")
    public String save(@Valid @ModelAttribute("director") Director director, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/directors/form";
        }
        Director saved = directorService.save(director);
        return "redirect:/directors/" + saved.getId();
    }
}
