package it.uniroma3.siw.festivalcinema.dto;

import java.time.LocalDate;

import it.uniroma3.siw.festivalcinema.model.Review;

public record ReviewDto(Long id, String text, Integer rating, LocalDate date, String author, Long movieId) {

    // richiede una recensione caricata con utente e film
    public static ReviewDto from(Review r) {
        return new ReviewDto(r.getId(), r.getText(), r.getRating(), r.getDate(), r.getUser().getFullName(),
                r.getMovie().getId());
    }
}
