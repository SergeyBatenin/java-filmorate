package ru.yandex.practicum.filmorate.repository.director;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.SaveDataException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.BaseJdbcRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcDirectorRepository extends BaseJdbcRepository<Director> implements DirectorRepository {
    public JdbcDirectorRepository(NamedParameterJdbcOperations jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Director> getAll() {
        String getAllQuery = "SELECT * FROM DIRECTORS";
        return jdbc.query(getAllQuery, mapper);
    }

    @Override
    public Optional<Director> getById(int directorId) {
        try {
            String getByIdQuery = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = :directorId";
            return Optional.ofNullable(jdbc.queryForObject(getByIdQuery, Map.of("directorId", directorId), mapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Director create(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String createQuery = """
                INSERT INTO DIRECTORS (NAME)
                VALUES (:name);
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", director.getName());
        jdbc.update(createQuery, params, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            director.setId(id);
        } else {
            throw new SaveDataException("Не удалось сохранить данные:" + director);
        }

        return director;
    }

    @Override
    public Director update(Director director) {
        String updateQuery = """
                UPDATE DIRECTORS
                SET NAME = :name
                WHERE DIRECTOR_ID = :directorId;
                """;
        jdbc.update(updateQuery, Map.of("directorId", director.getId(), "name", director.getName()));

        return director;
    }

    @Override
    public void removeById(int directorId) {
        String deleteQuery = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = :directorId";

        jdbc.update(deleteQuery, Map.of("directorId", directorId));
    }
}
