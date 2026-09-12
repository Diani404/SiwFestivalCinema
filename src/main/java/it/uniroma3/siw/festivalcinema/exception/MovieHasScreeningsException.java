package it.uniroma3.siw.festivalcinema.exception;

public class MovieHasScreeningsException extends RuntimeException {

    public MovieHasScreeningsException(String movieTitle) {
        super("Il film '" + movieTitle + "' ha proiezioni programmate in questo festival: eliminarle prima di rimuoverlo");
    }
}
