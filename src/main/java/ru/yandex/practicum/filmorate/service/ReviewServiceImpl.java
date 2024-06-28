package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.feed.FeedRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final FeedRepository feedRepository;

    @Override
    public Review create(Review review) {
        checkFilmExist(review.getFilmId(), "CREATE");
        checkUserExist(review.getUserId(), "CREATE");
        Review createdReview = reviewRepository.create(review);
        feedRepository.saveEvent(createdReview.getUserId(), Operation.ADD, EventType.REVIEW, createdReview.getReviewId());
        return createdReview;
    }

    @Override
    public Review update(Review review) {
        checkReviewExist(review.getReviewId(), "UPDATE");
        checkFilmExist(review.getFilmId(), "UPDATE");
        checkUserExist(review.getUserId(), "UPDATE");
        Review updatedReview = reviewRepository.update(review);
        feedRepository.saveEvent(updatedReview.getUserId(), Operation.UPDATE, EventType.REVIEW, updatedReview.getReviewId());
        return updatedReview;
    }

    @Override
    public void delete(Long reviewId) {
        Optional<Review> deleteReviewOpt = reviewRepository.delete(reviewId);

        if (deleteReviewOpt.isPresent()) {
            Review review = deleteReviewOpt.get();
            feedRepository.saveEvent(review.getUserId(), Operation.REMOVE, EventType.REVIEW, reviewId);
        }
    }

    @Override
    public Review getById(Long reviewId) {
        return reviewRepository.getById(reviewId)
                .orElseThrow(() -> {
                    log.debug("GET-REVIEW-BY-ID. Отзыв с айди {} не найден", reviewId);
                    return new NotFoundException("Отзыв с id=" + reviewId + " не существует");
                });
    }

    @Override
    public Collection<Review> getReviews(Long filmId, int count) {
        if (filmId != null) {
            checkFilmExist(filmId, "GET-REVIEWS");
            return reviewRepository.getReviewsByFilmId(filmId, count);
        } else {
            return reviewRepository.getAllReviews(count);
        }
    }

    @Override
    public void addLikeToReview(Long reviewId, Long userId) {
        checkReviewExist(reviewId, "ADD-LIKE");
        checkUserExist(userId, "ADD-LIKE");
        reviewRepository.addLikeToReview(reviewId, userId);
    }

    @Override
    public void addDislikeToReview(Long reviewId, Long userId) {
        checkReviewExist(reviewId, "ADD-DISLIKE");
        checkUserExist(userId, "ADD-DISLIKE");
        reviewRepository.addDislikeToReview(reviewId, userId);
    }

    @Override
    public void deleteLikeFromReview(Long reviewId, Long userId) {
        checkReviewExist(reviewId, "DELETE-LIKE");
        checkUserExist(userId, "DELETE-LIKE");
        reviewRepository.deleteLikeFromReview(reviewId, userId);
    }

    @Override
    public void deleteDislikeFromReview(Long reviewId, Long userId) {
        checkReviewExist(reviewId, "DELETE-DISLIKE");
        checkUserExist(userId, "DELETE-DISLIKE");
        reviewRepository.deleteDislikeFromReview(reviewId, userId);
    }

    private void checkReviewExist(Long reviewId, String method) {
        reviewRepository.getById(reviewId).orElseThrow(() -> {
            log.debug("{}. Отзыв с id={} не найден", method, reviewId);
            return new NotFoundException("Отзыв с id=" + reviewId + " не существует");
        });
    }

    private void checkFilmExist(Long filmId, String method) {
        filmRepository.getById(filmId).orElseThrow(() -> {
            log.debug("{}. Фильм с id={} не найден", method, filmId);
            return new NotFoundException("Фильм с id=" + filmId + " не существует");
        });
    }

    private void checkUserExist(Long userId, String method) {
        userRepository.getById(userId).orElseThrow(() -> {
            log.info("{}. Пользователь с id={} не найден", method, userId);
            return new NotFoundException("Пользователь с id=" + userId + " не существует");
        });
    }
}
