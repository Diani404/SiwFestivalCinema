package it.uniroma3.siw.festivalcinema.dto;

import java.time.LocalDate;

import it.uniroma3.siw.festivalcinema.model.Festival;

public record FestivalDto(Long id, String name, Integer year, String city, LocalDate startDate, LocalDate endDate,
        String description) {

    public static FestivalDto from(Festival f) {
        return new FestivalDto(f.getId(), f.getName(), f.getYear(), f.getCity(), f.getStartDate(), f.getEndDate(),
                f.getDescription());
    }
}
