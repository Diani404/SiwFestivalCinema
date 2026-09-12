package it.uniroma3.siw.festivalcinema.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalcinema.exception.DuplicateReviewException;
import it.uniroma3.siw.festivalcinema.exception.ReviewNotOwnedException;
import it.uniroma3.siw.festivalcinema.model.Movie;
import it.uniroma3.siw.festivalcinema.model.Review;
import it.uniroma3.siw.festivalcinema.model.User;
import it.uniroma3.siw.festivalcinema.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<Review> findByMovieId(Long movieId) {
        return reviewRepository.findByMovieIdWithUser(movieId);
    }

    @Transactional(readOnly = true)
    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUserIdWithMovie(userId);
    }

    @Transactional(readOnly = true)
    public Page<Review> findByMovieId(Long movieId, Pageable pageable) {
        return reviewRepository.findByMovieIdWithUser(movieId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Review> findByUserId(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdWithMovie(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Review> findById(Long id) {
        return reviewRepository.findByIdWithUserAndMovie(id);
    }

    @Transactional(readOnly = true)
    public boolean hasReviewed(User user, Long movieId) {
        return reviewRepository.existsByUser_IdAndMovie_Id(user.getId(), movieId);
    }

    @Transactional(readOnly = true)
    public Double averageRating(Long movieId) {
        return reviewRepository.averageRatingByMovieId(movieId);
    }

    @Transactional
    public Review save(Review review, Movie movie, User user) throws DuplicateReviewException {
        if (reviewRepository.existsByUser_IdAndMovie_Id(user.getId(), movie.getId())) {
            throw new DuplicateReviewException(movie.getTitle());
        }
        review.setMovie(movie);
        review.setUser(user);
        review.setDate(LocalDate.now());
        return reviewRepository.save(review);
    }

    @Transactional
    public Review update(Long id, User user, String text, Integer rating) throws ReviewNotOwnedException {
        Review existing = reviewRepository.findByIdWithUserAndMovie(id).orElseThrow();
        checkOwner(existing, user);
        existing.setText(text);
        existing.setRating(rating);
        existing.setDate(LocalDate.now());
        return existing;
    }

    @Transactional
    public void delete(Long id, User user) throws ReviewNotOwnedException {
        Review existing = reviewRepository.findByIdWithUserAndMovie(id).orElseThrow();
        checkOwner(existing, user);
        reviewRepository.delete(existing);
    }

    private void checkOwner(Review review, User user) throws ReviewNotOwnedException {
        if (!review.getUser().getId().equals(user.getId())) {
            throw new ReviewNotOwnedException();
        }
    }
}
