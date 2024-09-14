package ru.yandex.practicum.filmorate.repository.review;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.SaveDataException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.BaseJdbcRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class JdbcReviewRepository extends BaseJdbcRepository<Review> implements ReviewRepository {

    public JdbcReviewRepository(NamedParameterJdbcOperations jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review create(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = """
                INSERT INTO REVIEWS (FILM_ID, USER_ID, CONTENT, IS_POSITIVE)
                VALUES (:filmId, :userId, :content, :isPositive);
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", review.getFilmId())
                .addValue("userId", review.getUserId())
                .addValue("content", review.getContent())
                .addValue("isPositive", review.getIsPositive());
        jdbc.update(sql, params, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            review.setReviewId(id);
        } else {
            throw new SaveDataException("Не удалось сохранить данные:" + review);
        }

        return review;
    }

    @Override
    public Review update(Review review) {
        final String sql = """
                UPDATE REVIEWS
                SET CONTENT = :content,
                    IS_POSITIVE = :isPositive
                WHERE REVIEW_ID = :reviewId;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("isPositive", review.getIsPositive())
                .addValue("reviewId", review.getReviewId());
        jdbc.update(sql, params);

        return getById(review.getReviewId()).get();
    }

    @Override
    public Optional<Review> delete(long id) {
        Optional<Review> optionalReview = getById(id);

        final String sql = "DELETE FROM REVIEWS WHERE REVIEW_ID = :reviewId;";
        jdbc.update(sql, new MapSqlParameterSource("reviewId", id));

        return optionalReview;
    }

    @Override
    public Optional<Review> getById(long id) {
        try {
            final String sql = "SELECT * FROM REVIEWS WHERE REVIEW_ID = :reviewId;";

            return Optional.ofNullable(jdbc.queryForObject(sql,
                    new MapSqlParameterSource("reviewId", id), mapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Review> getReviewsByFilmId(Long filmId, int count) {
        final String sql = """
                SELECT *
                FROM REVIEWS
                WHERE FILM_ID = :filmId
                ORDER BY USEFUL DESC
                LIMIT :count;
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("count", count);
        return jdbc.query(sql, params, mapper);
    }

    @Override
    public Collection<Review> getAllReviews(int count) {
        final String sql = """
                SELECT *
                FROM REVIEWS
                ORDER BY USEFUL DESC
                LIMIT :count;
                """;

        return jdbc.query(sql, new MapSqlParameterSource("count", count), mapper);
    }


    @Override
    public void addLikeToReview(long id, long userId) {
        addRatingToReview(id, userId, 1);
    }

    @Override
    public void addDislikeToReview(long id, long userId) {
        addRatingToReview(id, userId, -1);
    }

    @Override
    public void deleteLikeFromReview(long id, long userId) {
        deleteRatingFromReview(id, userId, 1);
    }

    @Override
    public void deleteDislikeFromReview(long id, long userId) {
        deleteRatingFromReview(id, userId, -1);
    }

    private void addRatingToReview(long reviewId, long userId, int rating) {
        final String sql = """
                MERGE INTO REVIEW_RATINGS (REVIEW_ID, USER_ID, RATING)
                KEY (REVIEW_ID, USER_ID)
                VALUES (:reviewId, :userId, :rating);
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("userId", userId)
                .addValue("rating", rating);

        jdbc.update(sql, params);
        updateReviewUseful(reviewId);
    }

    private void deleteRatingFromReview(long reviewId, long userId, int rating) {
        final String sql = """
                DELETE FROM REVIEW_RATINGS
                WHERE REVIEW_ID = :reviewId AND USER_ID = :userId AND RATING = :rating;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("userId", userId)
                .addValue("rating", rating);
        jdbc.update(sql, params);
        updateReviewUseful(reviewId);
    }

    private void updateReviewUseful(Long reviewId) {
        SqlParameterSource params = new MapSqlParameterSource().addValue("reviewId", reviewId);
        final String sqlGetUseful = """
                SELECT COALESCE(SUM(RATING), 0)
                FROM REVIEW_RATINGS
                WHERE REVIEW_ID = :reviewId;
                """;

        Integer useful = jdbc.queryForObject(sqlGetUseful, params, Integer.class);

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("useful", useful != null ? useful : 0);
        jdbc.update("UPDATE REVIEWS SET USEFUL = :useful WHERE REVIEW_ID = :reviewId", parameterSource);
    }
}

