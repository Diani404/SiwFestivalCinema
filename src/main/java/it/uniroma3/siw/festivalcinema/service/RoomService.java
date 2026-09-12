package it.uniroma3.siw.festivalcinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalcinema.model.Room;
import it.uniroma3.siw.festivalcinema.repository.RoomRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomRepository.findAllByOrderByName();
    }

    @Transactional(readOnly = true)
    public Page<Room> findAll(Pageable pageable) {
        return roomRepository.findAllByOrderByName(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    @Transactional
    public Room save(Room room) {
        if (room.getId() == null) {
            return roomRepository.save(room);
        }
        Room existing = roomRepository.findById(room.getId()).orElseThrow();
        existing.setName(room.getName());
        existing.setAddress(room.getAddress());
        existing.setCapacity(room.getCapacity());
        return existing;
    }
}
