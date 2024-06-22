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
        checkFilmExist(review.getFilmId());
        checkUserExist(review.getUserId());
        return reviewRepository.create(review);
    }

    @Override
    public Review update(Review review) {
        getById(review.getReviewId());
        checkFilmExist(review.getFilmId());
        checkUserExist(review.getUserId());
        return reviewRepository.update(review);
    }

    @Override
    public void delete(Long reviewId) {
        getById(reviewId);
        reviewRepository.delete(reviewId);
    }

    @Override
    public Review getById(Long reviewId) {
        return reviewRepository.getById(reviewId)
                .orElseThrow(() -> {
                    log.debug("Отзыв с айди {} не найден", reviewId);
                    return new NotFoundException("Отзыва с id=" + reviewId + " не существует");
                });
    }

    @Override
    public Collection<Review> getReviews(Long filmId, int count) {
        if (filmId != null) {
            checkFilmExist(filmId);
            return reviewRepository.getReviewsByFilmId(filmId, count);
        } else {
            return reviewRepository.getAllReviews(count);
        }
    }

    @Override
    public void addLikeToReview(Long reviewId, Long userId) {
        getById(reviewId);
        checkUserExist(userId);
        reviewRepository.addLikeToReview(reviewId, userId);
    }

    @Override
    public void addDislikeToReview(Long reviewId, Long userId) {
        getById(reviewId);
        checkUserExist(userId);
        reviewRepository.addDislikeToReview(reviewId, userId);
    }

    @Override
    public void deleteLikeFromReview(Long reviewId, Long userId) {
        getById(reviewId);
        checkUserExist(userId);
        reviewRepository.deleteLikeFromReview(reviewId, userId);
    }

    @Override
    public void deleteDislikeFromReview(Long reviewId, Long userId) {
        getById(reviewId);
        checkUserExist(userId);
        reviewRepository.deleteDislikeFromReview(reviewId, userId);
    }

    private void checkFilmExist(Long filmId) {
        userRepository.getById(filmId).orElseThrow(() -> {
            log.debug("Фильм с айди {} не найден", filmId);
            return new NotFoundException("Фильма с id=" + filmId + " не существует");
        });
    }

    private void checkUserExist(Long userId) {
        filmRepository.getById(userId).orElseThrow(() -> {
            log.debug("Пользователь с айди {} не найден", userId);
            return new NotFoundException("Пользователя с id=" + userId + " не существует");
        });
    }
}
