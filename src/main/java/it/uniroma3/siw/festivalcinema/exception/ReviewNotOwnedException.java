package it.uniroma3.siw.festivalcinema.exception;

public class ReviewNotOwnedException extends RuntimeException {

    public ReviewNotOwnedException() {
        super("Puoi modificare o eliminare solo le recensioni che tu hai scritto");
    }
}
