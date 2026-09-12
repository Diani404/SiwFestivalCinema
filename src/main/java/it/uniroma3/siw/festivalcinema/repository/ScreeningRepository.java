package it.uniroma3.siw.festivalcinema.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festivalcinema.model.Screening;
import it.uniroma3.siw.festivalcinema.model.ScreeningStatus;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Query("select s from Screening s join fetch s.movie m join fetch m.director join fetch s.room "
         + "where s.festival.id = :festivalId order by s.date, s.time")
    List<Screening> findByFestivalIdWithDetails(@Param("festivalId") Long festivalId);

    @Query("select s from Screening s join fetch s.movie m join fetch m.director join fetch s.room "
         + "where s.festival.id = :festivalId and s.date = :date order by s.time")
    List<Screening> findByFestivalIdAndDateWithDetails(@Param("festivalId") Long festivalId, @Param("date") LocalDate date);

    @Query(value = "select s from Screening s join fetch s.movie m join fetch m.director join fetch s.room "
                 + "where s.festival.id = :festivalId order by s.date, s.time",
           countQuery = "select count(s) from Screening s where s.festival.id = :festivalId")
    Page<Screening> findByFestivalIdWithDetails(@Param("festivalId") Long festivalId, Pageable pageable);

    @Query(value = "select s from Screening s join fetch s.movie m join fetch m.director join fetch s.room "
                 + "where s.festival.id = :festivalId and s.date = :date order by s.time",
           countQuery = "select count(s) from Screening s where s.festival.id = :festivalId and s.date = :date")
    Page<Screening> findByFestivalIdAndDateWithDetails(@Param("festivalId") Long festivalId,
            @Param("date") LocalDate date, Pageable pageable);

    @Query(value = "select s from Screening s join fetch s.room join fetch s.festival "
                 + "where s.movie.id = :movieId order by s.date, s.time",
           countQuery = "select count(s) from Screening s where s.movie.id = :movieId")
    Page<Screening> findByMovieIdWithDetails(@Param("movieId") Long movieId, Pageable pageable);

    //lazy
    List<Screening> findByFestival_IdOrderByDateAscTimeAsc(Long festivalId);

    @Query("select distinct s.date from Screening s where s.festival.id = :festivalId order by s.date")
    List<LocalDate> findDatesByFestivalId(@Param("festivalId") Long festivalId);

    @Query("select s from Screening s join fetch s.room join fetch s.festival "
         + "where s.movie.id = :movieId order by s.date, s.time")
    List<Screening> findByMovieIdWithDetails(@Param("movieId") Long movieId);

    @Query("select s from Screening s join fetch s.movie join fetch s.room join fetch s.festival where s.id = :id")
    Optional<Screening> findByIdWithDetails(@Param("id") Long id);

    @Query("select s from Screening s join fetch s.movie "
         + "where s.room.id = :roomId and s.date = :date and s.status <> :excludedStatus")
    List<Screening> findByRoomIdAndDate(@Param("roomId") Long roomId, @Param("date") LocalDate date,
            @Param("excludedStatus") ScreeningStatus excludedStatus);

    boolean existsByMovie_IdAndFestival_Id(Long movieId, Long festivalId);

}
