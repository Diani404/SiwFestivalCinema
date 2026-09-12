package it.uniroma3.siw.festivalcinema.controller.rest;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.festivalcinema.dto.ReviewDto;
import it.uniroma3.siw.festivalcinema.dto.ReviewRequest;
import it.uniroma3.siw.festivalcinema.model.Review;
import it.uniroma3.siw.festivalcinema.model.User;
import it.uniroma3.siw.festivalcinema.service.CredentialsService;
import it.uniroma3.siw.festivalcinema.service.ReviewService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class RestReviewController {

    private final ReviewService reviewService;
    private final CredentialsService credentialsService;

    public RestReviewController(ReviewService reviewService, CredentialsService credentialsService) {
        this.reviewService = reviewService;
        this.credentialsService = credentialsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> show(@PathVariable Long id) {
        Optional<Review> optional = reviewService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ReviewDto.from(optional.get()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> update(@PathVariable Long id, @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (reviewService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = credentialsService.getUser(userDetails.getUsername());
        Review updated = reviewService.update(id, user, request.text(), request.rating());
        return ResponseEntity.ok(ReviewDto.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (reviewService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = credentialsService.getUser(userDetails.getUsername());
        reviewService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
