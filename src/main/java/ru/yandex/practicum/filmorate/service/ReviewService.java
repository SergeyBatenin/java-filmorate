package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {
    Review create(Review review);

    Review update(Review review);

    void delete(Long id);

    Review getById(Long id);

    Collection<Review> getReviews(Long filmId, int count);

    void addLikeToReview(Long id, Long userId);

    void addDislikeToReview(Long id, Long userId);

    void deleteLikeFromReview(Long id, Long userId);

    void deleteDislikeFromReview(Long id, Long userId);
}
