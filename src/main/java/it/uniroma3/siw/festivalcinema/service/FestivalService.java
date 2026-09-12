package it.uniroma3.siw.festivalcinema.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalcinema.exception.DuplicateFestivalException;
import it.uniroma3.siw.festivalcinema.exception.MovieHasScreeningsException;
import it.uniroma3.siw.festivalcinema.model.Festival;
import it.uniroma3.siw.festivalcinema.model.Movie;
import it.uniroma3.siw.festivalcinema.repository.FestivalRepository;
import it.uniroma3.siw.festivalcinema.repository.MovieRepository;
import it.uniroma3.siw.festivalcinema.repository.ScreeningRepository;

@Service
public class FestivalService {

    private final FestivalRepository festivalRepository;
    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;
    private final PosterStorageService posterStorageService;

    public FestivalService(FestivalRepository festivalRepository, MovieRepository movieRepository,
            ScreeningRepository screeningRepository, PosterStorageService posterStorageService) {
        this.festivalRepository = festivalRepository;
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
        this.posterStorageService = posterStorageService;
    }

    @Transactional(readOnly = true)
    public List<Festival> findAll() {
        return festivalRepository.findAllByOrderByStartDateDesc();
    }

    @Transactional(readOnly = true)
    public Page<Festival> findAll(Pageable pageable) {
        return festivalRepository.findAllByOrderByStartDateDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Festival> findByMovieId(Long movieId, Pageable pageable) {
        return festivalRepository.findByMovieId(movieId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Festival> findById(Long id) {
        return festivalRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Festival> findByIdWithMovies(Long id) {
        return festivalRepository.findByIdWithMovies(id);
    }

    @Transactional(readOnly = true)
    public Page<Festival> findInProgress(Pageable pageable) {
        return festivalRepository.findInProgress(LocalDate.now(), pageable);
    }

    @Transactional
    public Festival save(Festival festival) throws DuplicateFestivalException {
        boolean duplicate = festival.getId() == null
            ? festivalRepository.existsByNameIgnoreCaseAndYear(festival.getName(), festival.getYear())
            : festivalRepository.existsByNameIgnoreCaseAndYearAndIdNot(festival.getName(), festival.getYear(), festival.getId());
        if (duplicate) {
            throw new DuplicateFestivalException(festival.getName(), festival.getYear());
        }
        if (festival.getId() == null) {
            return festivalRepository.save(festival);
        }
        Festival existing = festivalRepository.findById(festival.getId()).orElseThrow();
        existing.setName(festival.getName());
        existing.setYear(festival.getYear());
        existing.setCity(festival.getCity());
        existing.setStartDate(festival.getStartDate());
        existing.setEndDate(festival.getEndDate());
        existing.setDescription(festival.getDescription());
        // posterFileName e' null se nel form non e' stata caricata una nuova locandina
        if (festival.getPosterFileName() != null) {
            posterStorageService.delete(existing.getPosterFileName());
            existing.setPosterFileName(festival.getPosterFileName());
        }
        return existing;
    }

    @Transactional
    public void addMovie(Long festivalId, Long movieId) {
        Festival festival = festivalRepository.findById(festivalId).orElseThrow();
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        if (!festival.getMovies().contains(movie)) {
            festival.getMovies().add(movie);
        }
    }

    @Transactional
    public void removeMovie(Long festivalId, Long movieId) throws MovieHasScreeningsException {
        Festival festival = festivalRepository.findById(festivalId).orElseThrow();
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        if (screeningRepository.existsByMovie_IdAndFestival_Id(movieId, festivalId)) {
            throw new MovieHasScreeningsException(movie.getTitle());
        }
        festival.getMovies().remove(movie);
    }
}
