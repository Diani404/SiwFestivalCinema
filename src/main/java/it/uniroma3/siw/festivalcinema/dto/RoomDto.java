package it.uniroma3.siw.festivalcinema.dto;

import it.uniroma3.siw.festivalcinema.model.Room;

public record RoomDto(Long id, String name, String address, Integer capacity) {

    public static RoomDto from(Room r) {
        return new RoomDto(r.getId(), r.getName(), r.getAddress(), r.getCapacity());
    }
}
