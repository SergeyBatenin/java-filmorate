package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.BaseJdbcRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcGenreRepository extends BaseJdbcRepository<Genre> implements GenreRepository {
    public JdbcGenreRepository(NamedParameterJdbcOperations jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Genre> getById(int genreId) {
        try {
            String sqlQuery = "SELECT * FROM GENRES WHERE GENRE_ID = :genreId";
            return Optional.ofNullable(jdbc.queryForObject(sqlQuery, Map.of("genreId", genreId), mapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Genre> getAll() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbc.query(sqlQuery, mapper);
    }
}
