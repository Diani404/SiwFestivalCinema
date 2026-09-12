package it.uniroma3.siw.festivalcinema.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import it.uniroma3.siw.festivalcinema.model.Screening;

public record ScreeningDto(Long id, LocalDate date, LocalTime time, String status, MovieDto movie, RoomDto room) {

    // richiede una proiezione caricata con film, regista e sala
    public static ScreeningDto from(Screening s) {
        return new ScreeningDto(s.getId(), s.getDate(), s.getTime(), s.getStatus().name(),
                MovieDto.from(s.getMovie()), RoomDto.from(s.getRoom()));
    }
}
