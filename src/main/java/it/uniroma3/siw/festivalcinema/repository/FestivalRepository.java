package it.uniroma3.siw.festivalcinema.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festivalcinema.model.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    List<Festival> findAllByOrderByStartDateDesc();

    Page<Festival> findAllByOrderByStartDateDesc(Pageable pageable);

    //festival a cui partecipa un film
    @Query("select f from Festival f join f.movies m where m.id = :movieId order by f.startDate desc")
    Page<Festival> findByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    //festival in corso
    @Query("select f from Festival f where :today between f.startDate and f.endDate order by f.startDate")
    Page<Festival> findInProgress(@Param("today") LocalDate today, Pageable pageable);

    @Query("select distinct f from Festival f left join fetch f.movies m left join fetch m.director where f.id = :id")
    Optional<Festival> findByIdWithMovies(@Param("id") Long id);

    boolean existsByNameIgnoreCaseAndYear(String name, Integer year);

    boolean existsByNameIgnoreCaseAndYearAndIdNot(String name, Integer year, Long id);

}
