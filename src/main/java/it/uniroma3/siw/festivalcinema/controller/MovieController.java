package it.uniroma3.siw.festivalcinema.controller;

import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festivalcinema.model.Movie;
import it.uniroma3.siw.festivalcinema.model.User;
import it.uniroma3.siw.festivalcinema.service.CredentialsService;
import it.uniroma3.siw.festivalcinema.service.DirectorService;
import it.uniroma3.siw.festivalcinema.service.MovieService;
import it.uniroma3.siw.festivalcinema.service.ReviewService;
import it.uniroma3.siw.festivalcinema.service.FestivalService;
import it.uniroma3.siw.festivalcinema.service.ScreeningService;
import it.uniroma3.siw.festivalcinema.util.Pagination;
import jakarta.validation.Valid;

@Controller
public class MovieController {

    private final MovieService movieService;
    private final DirectorService directorService;
    private final ScreeningService screeningService;
    private final ReviewService reviewService;
    private final CredentialsService credentialsService;
    private final FestivalService festivalService;

    public MovieController(MovieService movieService, DirectorService directorService,
            ScreeningService screeningService, ReviewService reviewService, CredentialsService credentialsService,
            FestivalService festivalService) {
        this.movieService = movieService;
        this.directorService = directorService;
        this.screeningService = screeningService;
        this.reviewService = reviewService;
        this.credentialsService = credentialsService;
        this.festivalService = festivalService;
    }

    @GetMapping("/movies")
    public String list(@RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") Integer page, Model model) {
        model.addAttribute("movies", movieService.search(title, genre, Pagination.of(page)));
        model.addAttribute("title", title);
        model.addAttribute("genre", genre);
        return "movies/list";
    }

    @GetMapping("/movies/{id}")
    public String show(@PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer paginaFestival,
            @RequestParam(defaultValue = "0") Integer paginaProiezioni,
            @RequestParam(defaultValue = "0") Integer paginaRecensioni,
            Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Movie> optional = movieService.findByIdWithDirector(id);
        if (optional.isEmpty()) {
            return "redirect:/movies";
        }
        model.addAttribute("movie", optional.get());
        model.addAttribute("festivals", festivalService.findByMovieId(id, Pagination.of(paginaFestival)));
        model.addAttribute("screenings", screeningService.findByMovieId(id, Pagination.of(paginaProiezioni)));
        model.addAttribute("reviews", reviewService.findByMovieId(id, Pagination.of(paginaRecensioni)));
        model.addAttribute("averageRating", reviewService.averageRating(id));

        boolean canReview = false;
        if (userDetails != null) {
            User user = credentialsService.getUser(userDetails.getUsername());
            canReview = user != null && !reviewService.hasReviewed(user, id);
        }
        model.addAttribute("canReview", canReview);
        return "movies/show";
    }

    @GetMapping("/admin/movies/new")
    public String createForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("directors", directorService.findAll());
        return "admin/movies/form";
    }

    @GetMapping("/admin/movies/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Movie> optional = movieService.findByIdWithDirector(id);
        if (optional.isEmpty()) {
            return "redirect:/movies";
        }
        model.addAttribute("movie", optional.get());
        model.addAttribute("directors", directorService.findAll());
        return "admin/movies/form";
    }

    @PostMapping("/admin/movies")
    public String save(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("directors", directorService.findAll());
            return "admin/movies/form";
        }
        Movie saved = movieService.save(movie);
        return "redirect:/movies/" + saved.getId();
    }
}
