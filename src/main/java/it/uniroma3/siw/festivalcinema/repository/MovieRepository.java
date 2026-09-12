package it.uniroma3.siw.festivalcinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festivalcinema.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findAllByOrderByTitle();

    @EntityGraph(attributePaths = "director")
    List<Movie> findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseOrderByTitle(String title, String genre);

    @EntityGraph(attributePaths = "director")
    Page<Movie> findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseOrderByTitle(String title, String genre,
            Pageable pageable);

    //tutti i film di regista
    @EntityGraph(attributePaths = "director")
    Page<Movie> findByDirector_IdOrderByTitle(Long directorId, Pageable pageable);

    @Query("select m from Movie m join fetch m.director where m.id = :id")
    Optional<Movie> findByIdWithDirector(@Param("id") Long id);

    @Query("select distinct m from Movie m join fetch m.director left join fetch m.festivals where m.id = :id")
    Optional<Movie> findByIdWithDirectorAndFestivals(@Param("id") Long id);

    //film di regista lazy
    List<Movie> findByFestivals_IdOrderByTitle(Long festivalId);

    //join fetch
    @Query("select m from Movie m join fetch m.director join m.festivals f where f.id = :festivalId order by m.title")
    List<Movie> findByFestivalIdWithDirector(@Param("festivalId") Long festivalId);

    //join fetch x paginazione
    @Query(value = "select m from Movie m join fetch m.director join m.festivals f where f.id = :festivalId order by m.title",
           countQuery = "select count(m) from Movie m join m.festivals f where f.id = :festivalId")
    Page<Movie> findByFestivalIdWithDirector(@Param("festivalId") Long festivalId, Pageable pageable);

    //EntityGraph
    @EntityGraph(attributePaths = "director")
    @Query("select m from Movie m join m.festivals f where f.id = :festivalId order by m.title")
    List<Movie> findByFestivalIdWithEntityGraph(@Param("festivalId") Long festivalId);

}
