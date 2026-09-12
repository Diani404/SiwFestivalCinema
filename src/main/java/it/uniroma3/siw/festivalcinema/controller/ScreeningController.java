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

import it.uniroma3.siw.festivalcinema.exception.InvalidScreeningException;
import it.uniroma3.siw.festivalcinema.exception.RoomNotAvailableException;
import it.uniroma3.siw.festivalcinema.model.Festival;
import it.uniroma3.siw.festivalcinema.model.Screening;
import it.uniroma3.siw.festivalcinema.model.ScreeningStatus;
import it.uniroma3.siw.festivalcinema.service.FestivalService;
import it.uniroma3.siw.festivalcinema.service.MovieService;
import it.uniroma3.siw.festivalcinema.service.RoomService;
import it.uniroma3.siw.festivalcinema.service.ScreeningService;
import it.uniroma3.siw.festivalcinema.util.Pagination;
import jakarta.validation.Valid;

@Controller
public class ScreeningController {

    private final ScreeningService screeningService;
    private final FestivalService festivalService;
    private final MovieService movieService;
    private final RoomService roomService;

    public ScreeningController(ScreeningService screeningService, FestivalService festivalService,
            MovieService movieService, RoomService roomService) {
        this.screeningService = screeningService;
        this.festivalService = festivalService;
        this.movieService = movieService;
        this.roomService = roomService;
    }

    @GetMapping("/admin/festivals/{festivalId}/screenings")
    public String list(@PathVariable Long festivalId, @RequestParam(defaultValue = "0") Integer page, Model model) {
        Optional<Festival> optional = festivalService.findById(festivalId);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        model.addAttribute("festival", optional.get());
        model.addAttribute("screenings", screeningService.findByFestivalId(festivalId, Pagination.of(page)));
        return "admin/screenings/list";
    }

    @GetMapping("/admin/festivals/{festivalId}/screenings/new")
    public String createForm(@PathVariable Long festivalId, Model model) {
        Optional<Festival> optional = festivalService.findById(festivalId);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        model.addAttribute("screening", new Screening());
        addFormAttributes(model, optional.get());
        return "admin/screenings/form";
    }

    @PostMapping("/admin/festivals/{festivalId}/screenings")
    public String save(@PathVariable Long festivalId, @Valid @ModelAttribute("screening") Screening screening,
            BindingResult bindingResult, Model model) {
        Optional<Festival> optional = festivalService.findById(festivalId);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        Festival festival = optional.get();
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, festival);
            return "admin/screenings/form";
        }
        try {
            screening.setFestival(festival);
            screeningService.save(screening);
            return "redirect:/admin/festivals/" + festivalId + "/screenings";
        } catch (RoomNotAvailableException e) {
            bindingResult.reject("screening.roomNotAvailable", e.getMessage());
        } catch (InvalidScreeningException e) {
            bindingResult.reject("screening.invalid", e.getMessage());
        }
        addFormAttributes(model, festival);
        return "admin/screenings/form";
    }

    @GetMapping("/admin/screenings/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Screening> optional = screeningService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        Screening screening = optional.get();
        model.addAttribute("screening", screening);
        model.addAttribute("movie", screening.getMovie());
        addFormAttributes(model, screening.getFestival());
        return "admin/screenings/form";
    }

    @PostMapping("/admin/screenings/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("screening") Screening screening,
            BindingResult bindingResult, Model model) {
        Optional<Screening> optional = screeningService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        Screening existing = optional.get();
        screening.setId(id);
        screening.setFestival(existing.getFestival());
        screening.setMovie(existing.getMovie());
        Long festivalId = existing.getFestival().getId();

        if (bindingResult.hasErrors()) {
            model.addAttribute("movie", existing.getMovie());
            addFormAttributes(model, existing.getFestival());
            return "admin/screenings/form";
        }
        try {
            screeningService.save(screening);
            return "redirect:/admin/festivals/" + festivalId + "/screenings";
        } catch (RoomNotAvailableException e) {
            bindingResult.reject("screening.roomNotAvailable", e.getMessage());
        } catch (InvalidScreeningException e) {
            bindingResult.reject("screening.invalid", e.getMessage());
        }
        model.addAttribute("movie", existing.getMovie());
        addFormAttributes(model, existing.getFestival());
        return "admin/screenings/form";
    }

    @PostMapping("/admin/screenings/{id}/delete")
    public String delete(@PathVariable Long id) {
        Optional<Screening> optional = screeningService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/festivals";
        }
        Long festivalId = optional.get().getFestival().getId();
        screeningService.deleteById(id);
        return "redirect:/admin/festivals/" + festivalId + "/screenings";
    }

    private void addFormAttributes(Model model, Festival festival) {
        model.addAttribute("festival", festival);
        model.addAttribute("movies", movieService.findByFestivalId(festival.getId()));
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("statuses", ScreeningStatus.values());
    }
}
