package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Validated @RequestBody Review review) {
        log.info("POST /reviews request: {}", review);
        Review createdReview = reviewService.create(review);
        log.info("POST /reviews response: {}", createdReview);
        return createdReview;
    }

    @PutMapping
    public Review update(@Validated @RequestBody Review review) {
        log.info("PUT /reviews request: {}", review);
        Review updatedReview = reviewService.update(review);
        log.info("PUT /reviews response: {}", updatedReview);
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("DELETE /reviews/{} request", id);
        reviewService.delete(id);
        log.info("DELETE /reviews/{} response: success", id);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) {
        log.info("GET /reviews/{} request", id);
        Review review = reviewService.getById(id);
        log.info("GET /reviews/{} response: {}", id, review);
        return review;
    }

    @GetMapping
    public Collection<Review> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") int count) {
        log.info("GET /reviews?filmId={}, count={} request", filmId, count);
        Collection<Review> reviews = reviewService.getReviews(filmId, count);
        log.info("GET /reviews?filmId={}, count={} response: success", filmId, count);
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /reviews/{}/like/{} request", id, userId);
        reviewService.addLikeToReview(id, userId);
        log.info("PUT /reviews/{}/like/{} response: success", id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /reviews/{}/dislike/{} request", id, userId);
        reviewService.addDislikeToReview(id, userId);
        log.info("PUT /reviews/{}/dislike/{} response: success", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/like/{} request", id, userId);
        reviewService.deleteLikeFromReview(id, userId);
        log.info("DELETE /reviews/{}/like/{} response: success", id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/dislike/{} request", id, userId);
        reviewService.deleteDislikeFromReview(id, userId);
        log.info("DELETE /reviews/{}/dislike/{} response: success", id, userId);
    }
}
