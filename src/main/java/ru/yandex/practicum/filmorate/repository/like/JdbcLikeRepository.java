package ru.yandex.practicum.filmorate.repository.like;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.repository.BaseJdbcRepository;

import java.time.Instant;
import java.util.Map;

@Repository
public class JdbcLikeRepository extends BaseJdbcRepository<Like> implements LikeRepository {
    public JdbcLikeRepository(NamedParameterJdbcOperations jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void like(long filmId, long userId) {
        String query = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (:filmId, :userId);";
        jdbc.update(query, Map.of("filmId", filmId, "userId", userId));
        addUserEvent(userId, Operation.ADD, filmId);
    }

    @Override
    public void unlike(long filmId, long userId) {
        String query = "DELETE FROM LIKES WHERE FILM_ID = :filmId AND USER_ID = :userId;";
        jdbc.update(query, Map.of("filmId", filmId, "userId", userId));
        addUserEvent(userId, Operation.REMOVE, filmId);
    }

    private void addUserEvent(long userId, Operation operation, long entityId) {
        String sqlQuery = """
                INSERT INTO USER_EVENTS (TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID)
                VALUES (:timestamp, :userId, :eventType, :operation, :entityId);
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("timestamp", Instant.now().toEpochMilli())
                .addValue("userId", userId)
                .addValue("eventType", EventType.LIKE.name())
                .addValue("operation", operation.name())
                .addValue("entityId", entityId);
        jdbc.update(sqlQuery, params);
    }
}
