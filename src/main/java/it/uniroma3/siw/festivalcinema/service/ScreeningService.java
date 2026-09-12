package it.uniroma3.siw.festivalcinema.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalcinema.exception.InvalidScreeningException;
import it.uniroma3.siw.festivalcinema.exception.RoomNotAvailableException;
import it.uniroma3.siw.festivalcinema.model.Festival;
import it.uniroma3.siw.festivalcinema.model.Movie;
import it.uniroma3.siw.festivalcinema.model.Room;
import it.uniroma3.siw.festivalcinema.model.Screening;
import it.uniroma3.siw.festivalcinema.model.ScreeningStatus;
import it.uniroma3.siw.festivalcinema.repository.FestivalRepository;
import it.uniroma3.siw.festivalcinema.repository.MovieRepository;
import it.uniroma3.siw.festivalcinema.repository.RoomRepository;
import it.uniroma3.siw.festivalcinema.repository.ScreeningRepository;

@Service
public class ScreeningService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final ScreeningRepository screeningRepository;
    private final FestivalRepository festivalRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    public ScreeningService(ScreeningRepository screeningRepository, FestivalRepository festivalRepository,
            MovieRepository movieRepository, RoomRepository roomRepository) {
        this.screeningRepository = screeningRepository;
        this.festivalRepository = festivalRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<Screening> findByFestivalId(Long festivalId) {
        return screeningRepository.findByFestivalIdWithDetails(festivalId);
    }

    @Transactional(readOnly = true)
    public List<Screening> findByFestivalIdAndDate(Long festivalId, LocalDate date) {
        return screeningRepository.findByFestivalIdAndDateWithDetails(festivalId, date);
    }

    @Transactional(readOnly = true)
    public Page<Screening> findByFestivalId(Long festivalId, Pageable pageable) {
        return screeningRepository.findByFestivalIdWithDetails(festivalId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Screening> findByFestivalIdAndDate(Long festivalId, LocalDate date, Pageable pageable) {
        return screeningRepository.findByFestivalIdAndDateWithDetails(festivalId, date, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Screening> findByMovieId(Long movieId, Pageable pageable) {
        return screeningRepository.findByMovieIdWithDetails(movieId, pageable);
    }

    @Transactional(readOnly = true)
    public List<LocalDate> findDatesByFestivalId(Long festivalId) {
        return screeningRepository.findDatesByFestivalId(festivalId);
    }

    @Transactional(readOnly = true)
    public List<Screening> findByMovieId(Long movieId) {
        return screeningRepository.findByMovieIdWithDetails(movieId);
    }

    @Transactional(readOnly = true)
    public Optional<Screening> findById(Long id) {
        return screeningRepository.findByIdWithDetails(id);
    }

    //prima di salvare verifica se la sala è libera
    //se non va a buon fine salta tutto
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Screening save(Screening screening) throws RoomNotAvailableException, InvalidScreeningException {
        Festival festival = festivalRepository.findById(screening.getFestival().getId()).orElseThrow();
        Movie movie = movieRepository.findById(screening.getMovie().getId()).orElseThrow();
        Room room = roomRepository.findById(screening.getRoom().getId()).orElseThrow();

        if (!festival.getMovies().contains(movie)) {
            throw new InvalidScreeningException(
                    "Il film '" + movie.getTitle() + "' non partecipa al festival '" + festival.getName() + "'");
        }
        if (screening.getDate().isBefore(festival.getStartDate()) || screening.getDate().isAfter(festival.getEndDate())) {
            throw new InvalidScreeningException("La data e' fuori dal periodo del festival ("
                    + festival.getStartDate() + " - " + festival.getEndDate() + ")");
        }
        if (screening.getStatus() != ScreeningStatus.CANCELLED) {
            checkRoomAvailability(screening, movie, room);
        }

        if (screening.getId() == null) {
            screening.setFestival(festival);
            screening.setMovie(movie);
            screening.setRoom(room);
            return screeningRepository.save(screening);
        }
        Screening existing = screeningRepository.findById(screening.getId()).orElseThrow();
        existing.setRoom(room);
        existing.setDate(screening.getDate());
        existing.setTime(screening.getTime());
        existing.setStatus(screening.getStatus());
        return existing;
    }

    @Transactional
    public void deleteById(Long id) {
        screeningRepository.deleteById(id);
    }

    private void checkRoomAvailability(Screening screening, Movie movie, Room room) throws RoomNotAvailableException {
        LocalDateTime newStart = LocalDateTime.of(screening.getDate(), screening.getTime());
        LocalDateTime newEnd = newStart.plusMinutes(movie.getDuration());

        List<Screening> sameDay = screeningRepository.findByRoomIdAndDate(room.getId(), screening.getDate(),
                ScreeningStatus.CANCELLED);
        for (Screening other : sameDay) {
            if (other.getId().equals(screening.getId())) {
                continue;
            }
            LocalDateTime start = LocalDateTime.of(other.getDate(), other.getTime());
            LocalDateTime end = start.plusMinutes(other.getMovie().getDuration());
            if (newStart.isBefore(end) && start.isBefore(newEnd)) {
                throw new RoomNotAvailableException(room.getName(), other.getMovie().getTitle(),
                        start.toLocalTime().format(TIME_FORMAT), end.toLocalTime().format(TIME_FORMAT));
            }
        }
    }
}
