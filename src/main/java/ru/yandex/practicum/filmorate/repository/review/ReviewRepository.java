package ru.yandex.practicum.filmorate.repository.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewRepository {
    Review create(Review review);

    Review update(Review review);

    void delete(long id);

    Optional<Review> getById(long id);

    Collection<Review> getReviewsByFilmId(Long filmId, int count);

    Collection<Review> getAllReviews(int count);

    void addLikeToReview(long id, long userId);

    void addDislikeToReview(long id, long userId);

    void deleteLikeFromReview(long id, long userId);

    void deleteDislikeFromReview(long id, long userId);
}
