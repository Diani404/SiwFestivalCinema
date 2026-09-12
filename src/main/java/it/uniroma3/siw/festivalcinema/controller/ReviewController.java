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

import it.uniroma3.siw.festivalcinema.exception.DuplicateReviewException;
import it.uniroma3.siw.festivalcinema.exception.ReviewNotOwnedException;
import it.uniroma3.siw.festivalcinema.model.Movie;
import it.uniroma3.siw.festivalcinema.model.Review;
import it.uniroma3.siw.festivalcinema.model.User;
import it.uniroma3.siw.festivalcinema.service.CredentialsService;
import it.uniroma3.siw.festivalcinema.service.MovieService;
import it.uniroma3.siw.festivalcinema.service.ReviewService;
import it.uniroma3.siw.festivalcinema.util.Pagination;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final MovieService movieService;
    private final CredentialsService credentialsService;

    public ReviewController(ReviewService reviewService, MovieService movieService,
            CredentialsService credentialsService) {
        this.reviewService = reviewService;
        this.movieService = movieService;
        this.credentialsService = credentialsService;
    }

    @GetMapping("/reviews")
    public String list(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") Integer page, Model model) {
        User user = credentialsService.getUser(userDetails.getUsername());
        model.addAttribute("reviews", reviewService.findByUserId(user.getId(), Pagination.of(page)));
        return "reviews/list";
    }

    @GetMapping("/movies/{movieId}/reviews/new")
    public String createForm(@PathVariable Long movieId, Model model) {
        Optional<Movie> optional = movieService.findById(movieId);
        if (optional.isEmpty()) {
            return "redirect:/movies";
        }
        model.addAttribute("movie", optional.get());
        model.addAttribute("review", new Review());
        return "reviews/form";
    }

    @PostMapping("/movies/{movieId}/reviews")
    public String save(@PathVariable Long movieId, @Valid @ModelAttribute("review") Review review,
            BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Movie> optional = movieService.findById(movieId);
        if (optional.isEmpty()) {
            return "redirect:/movies";
        }
        Movie movie = optional.get();
        if (bindingResult.hasErrors()) {
            model.addAttribute("movie", movie);
            return "reviews/form";
        }
        try {
            User user = credentialsService.getUser(userDetails.getUsername());
            reviewService.save(review, movie, user);
            return "redirect:/movies/" + movieId;
        } catch (DuplicateReviewException e) {
            bindingResult.reject("review.duplicate", e.getMessage());
            model.addAttribute("movie", movie);
            return "reviews/form";
        }
    }

    @GetMapping("/reviews/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Review> optional = reviewService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/reviews";
        }
        Review review = optional.get();
        User user = credentialsService.getUser(userDetails.getUsername());
        if (!review.getUser().equals(user)) {
            throw new ReviewNotOwnedException();
        }
        model.addAttribute("review", review);
        model.addAttribute("movie", review.getMovie());
        return "reviews/form";
    }

    @PostMapping("/reviews/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("review") Review review,
            BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Review> optional = reviewService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/reviews";
        }
        Movie movie = optional.get().getMovie();
        if (bindingResult.hasErrors()) {
            review.setId(id);
            model.addAttribute("movie", movie);
            return "reviews/form";
        }
        User user = credentialsService.getUser(userDetails.getUsername());
        reviewService.update(id, user, review.getText(), review.getRating());
        return "redirect:/movies/" + movie.getId();
    }

    @PostMapping("/reviews/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = credentialsService.getUser(userDetails.getUsername());
        reviewService.delete(id, user);
        return "redirect:/reviews";
    }
}
