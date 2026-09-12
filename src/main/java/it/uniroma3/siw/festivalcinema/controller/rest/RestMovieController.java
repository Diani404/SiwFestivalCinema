package it.uniroma3.siw.festivalcinema.controller.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.festivalcinema.dto.MovieDto;
import it.uniroma3.siw.festivalcinema.dto.ReviewDto;
import it.uniroma3.siw.festivalcinema.dto.ReviewRequest;
import it.uniroma3.siw.festivalcinema.model.Movie;
import it.uniroma3.siw.festivalcinema.model.Review;
import it.uniroma3.siw.festivalcinema.model.User;
import it.uniroma3.siw.festivalcinema.service.CredentialsService;
import it.uniroma3.siw.festivalcinema.service.MovieService;
import it.uniroma3.siw.festivalcinema.service.ReviewService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movies")
public class RestMovieController {

    private final MovieService movieService;
    private final ReviewService reviewService;
    private final CredentialsService credentialsService;

    public RestMovieController(MovieService movieService, ReviewService reviewService,
            CredentialsService credentialsService) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.credentialsService = credentialsService;
    }

    @GetMapping
    public List<MovieDto> list(@RequestParam(required = false) String title,
            @RequestParam(required = false) String genre) {
        return movieService.search(title, genre).stream().map(MovieDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> show(@PathVariable Long id) {
        Optional<Movie> optional = movieService.findByIdWithDirector(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(MovieDto.from(optional.get()));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDto>> reviews(@PathVariable Long id) {
        if (movieService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reviewService.findByMovieId(id).stream().map(ReviewDto::from).toList());
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewDto> createReview(@PathVariable Long id, @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Movie> optional = movieService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = credentialsService.getUser(userDetails.getUsername());
        Review review = new Review();
        review.setText(request.text());
        review.setRating(request.rating());
        Review saved = reviewService.save(review, optional.get(), user);
        ReviewDto dto = new ReviewDto(saved.getId(), saved.getText(), saved.getRating(), saved.getDate(),
                user.getFullName(), id);
        return ResponseEntity.created(URI.create("/api/reviews/" + saved.getId())).body(dto);
    }
}
