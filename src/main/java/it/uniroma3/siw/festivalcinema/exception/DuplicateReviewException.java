package it.uniroma3.siw.festivalcinema.exception;

public class DuplicateReviewException extends RuntimeException {

    public DuplicateReviewException(String movieTitle) {
        super("Hai gia' recensito il film '" + movieTitle + "'");
    }
}