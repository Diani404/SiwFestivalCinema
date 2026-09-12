package it.uniroma3.siw.festivalcinema.analysis;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalcinema.model.Movie;
import it.uniroma3.siw.festivalcinema.model.Screening;
import it.uniroma3.siw.festivalcinema.repository.MovieRepository;
import it.uniroma3.siw.festivalcinema.repository.ScreeningRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;

@Service
public class FetchStrategyAnalysisService {

    @PersistenceContext
    private EntityManager entityManager;

    private final EntityManagerFactory entityManagerFactory;
    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;

    public FetchStrategyAnalysisService(EntityManagerFactory entityManagerFactory, MovieRepository movieRepository,
            ScreeningRepository screeningRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
    }

    //film di un festival con registi
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public StrategyResult moviesLazy(Long festivalId) {
        Statistics statistics = reset();
        long start = System.nanoTime();
        List<Movie> movies = movieRepository.findByFestivals_IdOrderByTitle(festivalId);
        touchDirectors(movies);
        return result("LAZY", movies.size(), statistics, start);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public StrategyResult moviesJoinFetch(Long festivalId) {
        Statistics statistics = reset();
        long start = System.nanoTime();
        List<Movie> movies = movieRepository.findByFestivalIdWithDirector(festivalId);
        touchDirectors(movies);
        return result("JOIN FETCH", movies.size(), statistics, start);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public StrategyResult moviesEntityGraph(Long festivalId) {
        Statistics statistics = reset();
        long start = System.nanoTime();
        List<Movie> movies = movieRepository.findByFestivalIdWithEntityGraph(festivalId);
        touchDirectors(movies);
        return result("ENTITY GRAPH", movies.size(), statistics, start);
    }

    //programma di un festival con film, registi e sale
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public StrategyResult screeningsLazy(Long festivalId) {
        Statistics statistics = reset();
        long start = System.nanoTime();
        List<Screening> screenings = screeningRepository.findByFestival_IdOrderByDateAscTimeAsc(festivalId);
        touchScreenings(screenings);
        return result("LAZY", screenings.size(), statistics, start);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public StrategyResult screeningsJoinFetch(Long festivalId) {
        Statistics statistics = reset();
        long start = System.nanoTime();
        List<Screening> screenings = screeningRepository.findByFestivalIdWithDetails(festivalId);
        touchScreenings(screenings);
        return result("JOIN FETCH", screenings.size(), statistics, start);
    }

    //accesso alle associazioni x caricamento LAZY
    private void touchDirectors(List<Movie> movies) {
        for (Movie movie : movies) {
            movie.getDirector().getFullName();
        }
    }

    private void touchScreenings(List<Screening> screenings) {
        for (Screening screening : screenings) {
            screening.getMovie().getTitle();
            screening.getMovie().getDirector().getFullName();
            screening.getRoom().getName();
        }
    }

    //persistence context e statistiche azzerati per cache vuota
    private Statistics reset() {
        entityManager.clear();
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
        return statistics;
    }

    private StrategyResult result(String strategy, int loaded, Statistics statistics, long start) {
        long millis = (System.nanoTime() - start) / 1_000_000;
        return new StrategyResult(strategy, loaded, statistics.getPrepareStatementCount(), millis);
    }
}
