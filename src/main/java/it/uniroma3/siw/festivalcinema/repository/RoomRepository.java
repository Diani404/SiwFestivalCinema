package it.uniroma3.siw.festivalcinema.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.festivalcinema.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByOrderByName();

    Page<Room> findAllByOrderByName(Pageable pageable);

}
