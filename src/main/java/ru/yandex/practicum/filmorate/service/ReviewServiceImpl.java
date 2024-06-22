package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    @Override
    public Review create(Review review) {
        checkFilmExist(review.getFilmId(), "CREATE");
        checkUserExist(review.getUserId(), "CREATE");
        return reviewRepository.create(review);
    }

    @Override
    public Review update(Review review) {
        checkReviewExist(review.getReviewId(), "UPDATE");
        checkFilmExist(review.getFilmId(), "UPDATE");
        checkUserExist(review.getUserId(), "UPDATE");
        return reviewRepository.update(review);
    }

    @Override
    public void delete(Long reviewId) {
        checkReviewExist(reviewId, "DELETE");
        reviewRepository.delete(reviewId);
    }

    @Override
    public Review getById(Long reviewId) {
        return reviewRepository.getById(reviewId)
                .orElseThrow(() -> {
                    log.debug("GET-BY-ID. Отзыв с айди {} не найден", reviewId);
                    return new NotFoundException("Отзыва с id=" + reviewId + " не существует");
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
            return new NotFoundException("Отзыва с id=" + reviewId + " не существует");
        });
    }

    private void checkFilmExist(Long filmId, String method) {
        filmRepository.getById(filmId).orElseThrow(() -> {
            log.debug("{}. Фильм с id={} не найден", method, filmId);
            return new NotFoundException("Фильма с id=" + filmId + " не существует");
        });
    }

    private void checkUserExist(Long userId, String method) {
        userRepository.getById(userId).orElseThrow(() -> {
            log.info("{}. Пользователь с id={} не найден", method, userId);
            return new NotFoundException("Пользователя с id=" + userId + " не существует");
        });
    }
}
