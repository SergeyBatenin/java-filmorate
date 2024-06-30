package ru.yandex.practicum.filmorate.repository.like;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.repository.BaseJdbcRepository;

import java.util.Map;

@Repository
public class JdbcLikeRepository extends BaseJdbcRepository<Like> implements LikeRepository {
    public JdbcLikeRepository(NamedParameterJdbcOperations jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void like(long filmId, long userId) {
        String query = """
                MERGE INTO LIKES (FILM_ID, USER_ID)
                KEY (FILM_ID, USER_ID)
                VALUES (:filmId, :userId);
                """;
        jdbc.update(query, Map.of("filmId", filmId, "userId", userId));
    }

    @Override
    public void unlike(long filmId, long userId) {
        String query = "DELETE FROM LIKES WHERE FILM_ID = :filmId AND USER_ID = :userId;";
        jdbc.update(query, Map.of("filmId", filmId, "userId", userId));
    }
}
