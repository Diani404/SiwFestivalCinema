package it.uniroma3.siw.festivalcinema.exception;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String username) {
        super("Lo username '" + username + "' e' gia' in uso");
    }
}
