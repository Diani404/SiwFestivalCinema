package it.uniroma3.siw.festivalcinema.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.festivalcinema.exception.DuplicateFestivalException;
import it.uniroma3.siw.festivalcinema.exception.InvalidPosterException;
import it.uniroma3.siw.festivalcinema.exception.MovieHasScreeningsException;
import it.uniroma3.siw.festivalcinema.model.Festival;
import it.uniroma3.siw.festivalcinema.service.FestivalService;
import it.uniroma3.siw.festivalcinema.service.MovieService;
import it.uniroma3.siw.festivalcinema.service.PosterStorageService;
import it.uniroma3.siw.festivalcinema.service.ScreeningService;
import it.uniroma3.siw.festivalcinema.util.Pagination;
import jakarta.validation.Valid;

@Controller
public class FestivalController {

    private final FestivalService festivalService;
    private final MovieService movieService;
    private final ScreeningService screeningService;
    private final PosterStorageService posterStorageService;

    public FestivalController(FestivalService festivalService, MovieService movieService,
            ScreeningService screeningService, PosterStorageService posterStorageService) {
        this.festivalService = festivalService;
        this.movieService = movieService;
        this.screeningService = screeningService;
        this.posterStorageService = posterStorageService;
    }

    @GetMapping("/festivals")
    public String list(@RequestParam(defaultValue = "0") Integer page, Model model) {
        model.addAttribute("festivals", festivalService.findAll(Pagination.of(page)));
        return "festivals/list";
    }

    @GetMapping("/festivals/{id}")
    public String show(@PathVariable Long id, @RequestParam(defaultValue = "0") Integer page, Model model) {
        Optional<Festival> optional = festivalService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        model.addAttribute("festival", optional.get());
        // i film non arrivano piu' dalla collezione dell'entita' ma da una query paginata
        model.addAttribute("movies", movieService.findByFestivalId(id, Pagination.of(page)));
        return "festivals/show";
    }

    @GetMapping("/festivals/{id}/movies")
    public String movies(@PathVariable Long id, @RequestParam(defaultValue = "0") Integer page, Model model) {
        Optional<Festival> optional = festivalService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        model.addAttribute("festival", optional.get());
        model.addAttribute("movies", movieService.findByFestivalId(id, Pagination.of(page)));
        return "movies/list";
    }

    @GetMapping("/festivals/{id}/screenings")
    public String screenings(@PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") Integer page, Model model) {
        Optional<Festival> optional = festivalService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        model.addAttribute("festival", optional.get());
        model.addAttribute("dates", screeningService.findDatesByFestivalId(id));
        model.addAttribute("selectedDate", date);
        model.addAttribute("screenings", date == null
                ? screeningService.findByFestivalId(id, Pagination.of(page))
                : screeningService.findByFestivalIdAndDate(id, date, Pagination.of(page)));
        return "festivals/program";
    }

    // versione React del programma: i dati arrivano dalle API REST
    @GetMapping("/festivals/{id}/program")
    public String program(@PathVariable Long id, Model model) {
        Optional<Festival> optional = festivalService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        model.addAttribute("festival", optional.get());
        return "festivals/programReact";
    }

    @GetMapping("/admin/festivals/new")
    public String createForm(Model model) {
        model.addAttribute("festival", new Festival());
        return "admin/festivals/form";
    }

    @GetMapping("/admin/festivals/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Festival> optional = festivalService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        model.addAttribute("festival", optional.get());
        return "admin/festivals/form";
    }

    @PostMapping("/admin/festivals")
    public String save(@Valid @ModelAttribute("festival") Festival festival, BindingResult bindingResult,
            @RequestParam(value = "poster", required = false) MultipartFile poster) {
        if (bindingResult.hasErrors()) {
            return "admin/festivals/form";
        }
        try {
            //il file non è un entita
            if (poster != null && !poster.isEmpty()) {
                festival.setPosterFileName(posterStorageService.save(poster));
            } else {
                festival.setPosterFileName(null);
            }
            Festival saved = festivalService.save(festival);
            return "redirect:/festivals/" + saved.getId();
        } catch (DuplicateFestivalException e) {
            bindingResult.reject("festival.duplicate", e.getMessage());
            return "admin/festivals/form";
        } catch (InvalidPosterException e) {
            bindingResult.reject("festival.poster", e.getMessage());
            return "admin/festivals/form";
        }
    }

    @GetMapping("/admin/festivals/{id}/movies")
    public String manageMovies(@PathVariable Long id, @RequestParam(defaultValue = "0") Integer page, Model model) {
        Optional<Festival> optional = festivalService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        model.addAttribute("festival", optional.get());
        model.addAttribute("movies", movieService.findByFestivalId(id, Pagination.of(page)));
        model.addAttribute("allMovies", movieService.findAll());
        return "admin/festivals/movies";
    }

    @PostMapping("/admin/festivals/{id}/movies")
    public String addMovie(@PathVariable Long id, @RequestParam Long movieId) {
        festivalService.addMovie(id, movieId);
        return "redirect:/admin/festivals/" + id + "/movies";
    }

    @PostMapping("/admin/festivals/{id}/movies/{movieId}/remove")
    public String removeMovie(@PathVariable Long id, @PathVariable Long movieId, Model model) {
        try {
            festivalService.removeMovie(id, movieId);
            return "redirect:/admin/festivals/" + id + "/movies";
        } catch (MovieHasScreeningsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return manageMovies(id, 0, model);
        }
    }
}
