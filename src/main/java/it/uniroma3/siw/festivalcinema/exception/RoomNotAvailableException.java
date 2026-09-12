package it.uniroma3.siw.festivalcinema.exception;

public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException(String roomName, String movieTitle, String from, String to) {
        super("La sala " + roomName + " e' gia' occupata dalle " + from + " alle " + to
                + " dalla proiezione di '" + movieTitle + "'");
    }
}
