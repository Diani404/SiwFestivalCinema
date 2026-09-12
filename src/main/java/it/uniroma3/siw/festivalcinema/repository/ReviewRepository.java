package it.uniroma3.siw.festivalcinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festivalcinema.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUser_IdAndMovie_Id(Long userId, Long movieId);

    @Query("select r from Review r join fetch r.user where r.movie.id = :movieId order by r.date desc")
    List<Review> findByMovieIdWithUser(@Param("movieId") Long movieId);

    @Query("select r from Review r join fetch r.movie where r.user.id = :userId order by r.date desc")
    List<Review> findByUserIdWithMovie(@Param("userId") Long userId);

    @Query(value = "select r from Review r join fetch r.user where r.movie.id = :movieId order by r.date desc",
           countQuery = "select count(r) from Review r where r.movie.id = :movieId")
    Page<Review> findByMovieIdWithUser(@Param("movieId") Long movieId, Pageable pageable);

    @Query(value = "select r from Review r join fetch r.movie where r.user.id = :userId order by r.date desc",
           countQuery = "select count(r) from Review r where r.user.id = :userId")
    Page<Review> findByUserIdWithMovie(@Param("userId") Long userId, Pageable pageable);

    @Query("select r from Review r join fetch r.user join fetch r.movie where r.id = :id")
    Optional<Review> findByIdWithUserAndMovie(@Param("id") Long id);

    @Query("select avg(r.rating) from Review r where r.movie.id = :movieId")
    Double averageRatingByMovieId(@Param("movieId") Long movieId);

}
