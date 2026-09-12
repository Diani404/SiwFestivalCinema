package it.uniroma3.siw.festivalcinema.dto;

import it.uniroma3.siw.festivalcinema.model.Director;

public record DirectorDto(Long id, String name, String surname, String fullName, String nationality) {

    public static DirectorDto from(Director d) {
        return new DirectorDto(d.getId(), d.getName(), d.getSurname(), d.getFullName(), d.getNationality());
    }
}
