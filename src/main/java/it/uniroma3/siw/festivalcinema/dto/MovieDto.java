package it.uniroma3.siw.festivalcinema.dto;

import it.uniroma3.siw.festivalcinema.model.Movie;

public record MovieDto(Long id, String title, Integer year, Integer duration, String genre,
        String productionCountry, DirectorDto director) {

    // richiede un film caricato con il regista (join fetch o EntityGraph)
    public static MovieDto from(Movie m) {
        return new MovieDto(m.getId(), m.getTitle(), m.getYear(), m.getDuration(), m.getGenre(),
                m.getProductionCountry(), DirectorDto.from(m.getDirector()));
    }
}
