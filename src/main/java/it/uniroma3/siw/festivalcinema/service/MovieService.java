package it.uniroma3.siw.festivalcinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalcinema.model.Movie;
import it.uniroma3.siw.festivalcinema.repository.MovieRepository;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public List<Movie> findAll() {
        return movieRepository.findAllByOrderByTitle();
    }

    @Transactional(readOnly = true)
    public List<Movie> search(String title, String genre) {
        String t = title == null ? "" : title.trim();
        String g = genre == null ? "" : genre.trim();
        return movieRepository.findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseOrderByTitle(t, g);
    }

    @Transactional(readOnly = true)
    public Page<Movie> search(String title, String genre, Pageable pageable) {
        String t = title == null ? "" : title.trim();
        String g = genre == null ? "" : genre.trim();
        return movieRepository.findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseOrderByTitle(t, g, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> findByDirectorId(Long directorId, Pageable pageable) {
        return movieRepository.findByDirector_IdOrderByTitle(directorId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Movie> findByIdWithDirector(Long id) {
        return movieRepository.findByIdWithDirector(id);
    }

    @Transactional(readOnly = true)
    public Optional<Movie> findByIdWithDirectorAndFestivals(Long id) {
        return movieRepository.findByIdWithDirectorAndFestivals(id);
    }

    @Transactional(readOnly = true)
    public List<Movie> findByFestivalId(Long festivalId) {
        return movieRepository.findByFestivalIdWithDirector(festivalId);
    }

    @Transactional(readOnly = true)
    public Page<Movie> findByFestivalId(Long festivalId, Pageable pageable) {
        return movieRepository.findByFestivalIdWithDirector(festivalId, pageable);
    }

    @Transactional
    public Movie save(Movie movie) {
        if (movie.getId() == null) {
            return movieRepository.save(movie);
        }
        Movie existing = movieRepository.findById(movie.getId()).orElseThrow();
        existing.setTitle(movie.getTitle());
        existing.setYear(movie.getYear());
        existing.setDuration(movie.getDuration());
        existing.setGenre(movie.getGenre());
        existing.setProductionCountry(movie.getProductionCountry());
        existing.setDirector(movie.getDirector());
        return existing;
    }
}
