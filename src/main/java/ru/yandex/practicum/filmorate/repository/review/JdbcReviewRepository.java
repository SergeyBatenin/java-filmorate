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
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.BaseJdbcRepository;

import java.time.Instant;
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
            addUserEvent(review.getUserId(), Operation.ADD, review.getReviewId());
        } else {
            throw new SaveDataException("Не удалось сохранить данные:" + review);
        }

        return review;
    }

    @Override
    public Review update(Review review) {
        final String sql = """
                UPDATE REVIEWS
                SET
                    CONTENT = :content,
                    IS_POSITIVE = :isPositive
                WHERE REVIEW_ID = :reviewId;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("isPositive", review.getIsPositive())
                .addValue("reviewId", review.getReviewId());
        jdbc.update(sql, params);

        addUserEvent(review.getUserId(), Operation.UPDATE, review.getReviewId());

        return getById(review.getReviewId()).get();
    }

    @Override
    public void delete(long id) {
        final String sql = "DELETE FROM REVIEWS WHERE REVIEW_ID = :reviewId;";
        Optional<Review> optionalReview = getById(id);

        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            addUserEvent(review.getUserId(), Operation.REMOVE, id);
        }

        jdbc.update(sql, new MapSqlParameterSource("reviewId", id));
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
        final String sql = """
                INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID) VALUES (:reviewId, :userId);
                UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = :reviewId;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", id)
                .addValue("userId", userId);

        if (dislikeExists(id, userId)) {
            deleteDislikeFromReview(id, userId);
        }

        jdbc.update(sql, params);
        updateReviewUseful(id);
    }

    @Override
    public void addDislikeToReview(long id, long userId) {
        final String sql = """
                INSERT INTO REVIEW_DISLIKES (REVIEW_ID, USER_ID) VALUES (:reviewId, :userId);
                UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = :reviewId;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", id)
                .addValue("userId", userId);

        if (likeExists(id, userId)) {
            deleteLikeFromReview(id, userId);
        }

        jdbc.update(sql, params);
        updateReviewUseful(id);
    }

    @Override
    public void deleteLikeFromReview(long id, long userId) {
        final String sql = """
                DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = :reviewId AND USER_ID = :userId;
                UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = :reviewId;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", id)
                .addValue("userId", userId);
        jdbc.update(sql, params);
        updateReviewUseful(id);
    }

    @Override
    public void deleteDislikeFromReview(long id, long userId) {
        final String sql = """
                DELETE FROM REVIEW_DISLIKES WHERE REVIEW_ID = :reviewId AND USER_ID = :userId;
                UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = :reviewId;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", id)
                .addValue("userId", userId);
        jdbc.update(sql, params);
        updateReviewUseful(id);
    }

    private boolean likeExists(Long reviewId, Long userId) {
        final String sql = """
                    SELECT COUNT(*)
                    FROM REVIEW_LIKES
                    WHERE REVIEW_ID = :reviewId AND USER_ID = :userId;
                    """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("userId", userId);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    private boolean dislikeExists(Long reviewId, Long userId) {
        final String sql = """
                SELECT COUNT(*)
                FROM REVIEW_DISLIKES
                WHERE REVIEW_ID = :reviewId AND USER_ID = :userId;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("userId", userId);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    private void updateReviewUseful(Long reviewId) {
        SqlParameterSource params = new MapSqlParameterSource().addValue("reviewId", reviewId);
        final String sqlGetLikeCount = "SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = :reviewId;";
        Integer likeCount = jdbc.queryForObject(sqlGetLikeCount, params, Integer.class);
        if (likeCount == null) {
            likeCount = 0;
        }

        final String sqlGetDislikeCount = "SELECT COUNT(*) FROM REVIEW_DISLIKES WHERE REVIEW_ID = :reviewId;";
        Integer dislikeCount = jdbc.queryForObject(sqlGetDislikeCount, params, Integer.class);
        if (dislikeCount == null) {
            dislikeCount = 0;
        }

        int useful = likeCount - dislikeCount;

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("useful", useful);
        jdbc.update("UPDATE REVIEWS SET USEFUL = :useful WHERE REVIEW_ID = :reviewId", parameterSource);
    }

    private void addUserEvent(long userId, Operation operation, long entityId) {
        String sqlQuery = """
                INSERT INTO USER_EVENTS (TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID)
                VALUES (:timestamp, :userId, :eventType, :operation, :entityId);
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("timestamp", Instant.now().toEpochMilli())
                .addValue("userId", userId)
                .addValue("eventType", EventType.REVIEW.name())
                .addValue("operation", operation.name())
                .addValue("entityId", entityId);
        jdbc.update(sqlQuery, params);
    }
}

