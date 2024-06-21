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
        filmRepository.getById(review.getFilmId())
                .orElseThrow(() -> {
                    log.debug("CREATE {}. Фильм с id={} не найден", review, review.getFilmId());
                    return new NotFoundException("Фильма с id=" + review.getFilmId() + " не существует");
                });
        userRepository.getById(review.getUserId())
                .orElseThrow(() -> {
                    log.debug("CREATE {}. Пользователь с id={} не найден", review, review.getUserId());
                    return new NotFoundException("Пользователя с id=" + review.getUserId() + " не существует");
                });
        return reviewRepository.create(review);
    }

    @Override
    public Review update(Review review) {
        reviewRepository.getById(review.getReviewId())
                .orElseThrow(() -> {
                    log.debug("UPDATE {}. Отзыв с id={} не найден", review, review.getReviewId());
                    return new NotFoundException("Отзыва с id=" + review.getReviewId() + " не существует");
                });
        filmRepository.getById(review.getFilmId())
                .orElseThrow(() -> {
                    log.debug("UPDATE {}. Фильм с id={} не найден", review, review.getFilmId());
                    return new NotFoundException("Фильма с id=" + review.getFilmId() + " не существует");
                });
        userRepository.getById(review.getUserId())
                .orElseThrow(() -> {
                    log.debug("UPDATE {}. Пользователь с id={} не найден", review, review.getUserId());
                    return new NotFoundException("Пользователя с id=" + review.getUserId() + " не существует");
                });
        return reviewRepository.update(review);
    }

    @Override
    public void delete(Long id) {
        reviewRepository.getById(id)
                .orElseThrow(() -> {
                    log.debug("DELETE. Отзыв с id={} не найден", id);
                    return new NotFoundException("Отзыва с id=" + id + " не существует");
                });
        reviewRepository.delete(id);
    }

    @Override
    public Review getById(Long id) {
        return reviewRepository.getById(id)
                .orElseThrow(() -> {
                    log.debug("GET Review By ID {}. Отзыв с айди {} не найден", id, id);
                    return new NotFoundException("Отзыва с id=" + id + " не существует");
                });
    }

    @Override
    public Collection<Review> getReviews(Long filmId, int count) {
        if (filmId != null) {
            return reviewRepository.getReviewsByFilmId(filmId, count);
        } else {
            return reviewRepository.getAllReviews(count);
        }
    }

    @Override
    public void addLikeToReview(Long id, Long userId) {
        reviewRepository.getById(id)
                .orElseThrow(() -> {
                    log.debug("ADD-LIKE-REVIEW {}<-{}. Отзыв с id={} не найден", id, userId, id);
                    return new NotFoundException("Отзыва с id=" + id + " не существует");
                });
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("ADD-LIKE-REVIEW {}<-{}. Пользователь с id={} не найден", id, userId, userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        reviewRepository.addLikeToReview(id, userId);
    }

    @Override
    public void addDislikeToReview(Long id, Long userId) {
        reviewRepository.getById(id)
                .orElseThrow(() -> {
                    log.debug("ADD-DISLIKE-REVIEW {}<-{}. Отзыв с id={} не найден", id, userId, id);
                    return new NotFoundException("Отзыва с id=" + id + " не существует");
                });
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("ADD-DISLIKE-REVIEW {}<-{}. Пользователь с id={} не найден", id, userId, userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        reviewRepository.addDislikeToReview(id, userId);
    }

    @Override
    public void deleteLikeFromReview(Long id, Long userId) {
        reviewRepository.getById(id)
                .orElseThrow(() -> {
                    log.debug("DELETE-LIKE-REVIEW {}<-{}. Отзыв с id={} не найден", id, userId, id);
                    return new NotFoundException("Отзыва с id=" + id + " не существует");
                });
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("DELETE-LIKE-REVIEW {}<-{}. Пользователь с id={} не найден", id, userId, userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        reviewRepository.deleteLikeFromReview(id, userId);
    }

    @Override
    public void deleteDislikeFromReview(Long id, Long userId) {
        reviewRepository.getById(id)
                .orElseThrow(() -> {
                    log.debug("DELETE-DISLIKE-REVIEW {}<-{}. Отзыв с id={} не найден", id, userId, id);
                    return new NotFoundException("Отзыва с id=" + id + " не существует");
                });
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("DELETE-DISLIKE-REVIEW {}<-{}. Пользователь с id={} не найден", id, userId, userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        reviewRepository.deleteDislikeFromReview(id, userId);
    }
}
