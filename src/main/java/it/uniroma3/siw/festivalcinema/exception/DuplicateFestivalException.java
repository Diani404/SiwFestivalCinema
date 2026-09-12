package it.uniroma3.siw.festivalcinema.exception;

public class DuplicateFestivalException extends RuntimeException {

    public DuplicateFestivalException(String name, Integer year) {
        super("Il festival '" + name + "' (" + year + ") e' gia' presente nel sistema");
    }
}
