package it.uniroma3.siw.festivalcinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festivalcinema.model.Director;

public interface DirectorRepository extends JpaRepository<Director, Long> {

    List<Director> findAllByOrderBySurnameAscNameAsc();

    Page<Director> findAllByOrderBySurnameAscNameAsc(Pageable pageable);

    @Query("select distinct d from Director d left join fetch d.movies where d.id = :id")
    Optional<Director> findByIdWithMovies(@Param("id") Long id);

}
