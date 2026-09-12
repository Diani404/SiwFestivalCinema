package it.uniroma3.siw.festivalcinema.controller.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.festivalcinema.dto.FestivalDto;
import it.uniroma3.siw.festivalcinema.dto.MovieDto;
import it.uniroma3.siw.festivalcinema.dto.ScreeningDto;
import it.uniroma3.siw.festivalcinema.model.Festival;
import it.uniroma3.siw.festivalcinema.model.Screening;
import it.uniroma3.siw.festivalcinema.service.FestivalService;
import it.uniroma3.siw.festivalcinema.service.MovieService;
import it.uniroma3.siw.festivalcinema.service.ScreeningService;

@RestController
@RequestMapping("/api/festivals")
public class  RestFestivalController {

    private final FestivalService festivalService;
    private final MovieService movieService;
    private final ScreeningService screeningService;

    public RestFestivalController(FestivalService festivalService, MovieService movieService,
            ScreeningService screeningService) {
        this.festivalService = festivalService;
        this.movieService = movieService;
        this.screeningService = screeningService;
    }

    @GetMapping
    public List<FestivalDto> list() {
        return festivalService.findAll().stream().map(FestivalDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FestivalDto> show(@PathVariable Long id) {
        Optional<Festival> optional = festivalService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(FestivalDto.from(optional.get()));
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieDto>> movies(@PathVariable Long id) {
        if (festivalService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movieService.findByFestivalId(id).stream().map(MovieDto::from).toList());
    }

    @GetMapping("/{id}/screenings")
    public ResponseEntity<List<ScreeningDto>> screenings(@PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (festivalService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Screening> screenings = date == null
                ? screeningService.findByFestivalId(id)
                : screeningService.findByFestivalIdAndDate(id, date);
        return ResponseEntity.ok(screenings.stream().map(ScreeningDto::from).toList());
    }

    @GetMapping("/{id}/screening-dates")
    public ResponseEntity<List<LocalDate>> screeningDates(@PathVariable Long id) {
        if (festivalService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(screeningService.findDatesByFestivalId(id));
    }
}
