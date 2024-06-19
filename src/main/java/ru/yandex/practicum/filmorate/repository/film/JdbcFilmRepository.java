package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.SaveDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.BaseJdbcRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreMapper;
import ru.yandex.practicum.filmorate.repository.mpa.MpaMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Primary
public class JdbcFilmRepository extends BaseJdbcRepository<Film> implements FilmRepository {

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = """
                INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
                VALUES (:name, :description, :releaseDate, :duration, :mpaId);
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa().getId());
        jdbc.update(sqlQuery, params, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            film.setId(id);
        } else {
            throw new SaveDataException("Не удалось сохранить данные:" + film);
        }

        insertFilmGenres(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = """
                UPDATE FILMS
                SET NAME = :name,
                    DESCRIPTION = :description,
                    RELEASE_DATE = :releaseDate,
                    DURATION = :duration,
                    MPA_ID = :mpaId
                WHERE FILM_ID = :filmId;
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa().getId());
        jdbc.update(sqlQuery, params);

        String removeFilmGenresQuery = "DELETE FROM FILMS_GENRES WHERE FILM_ID = :filmId";
        jdbc.update(removeFilmGenresQuery, Map.of("filmId", film.getId()));

        insertFilmGenres(film);

        return film;
    }

    private void insertFilmGenres(Film film) {
        Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }

        String insertFilmGenresQuery = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (:filmId, :genreId);";
        List<MapSqlParameterSource> genreParams = genres.stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("filmId", film.getId())
                        .addValue("genreId", genre.getId()))
                .toList();
        jdbc.batchUpdate(insertFilmGenresQuery, genreParams.toArray(new MapSqlParameterSource[0]));
    }

    @Override
    public Collection<Film> getAll() {
        // получить все жанры
        Map<Integer, Genre> genres = getIdsGenresMap();
        // получить фильмы
        String getFilmsQuery = """
                SELECT F.FilM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID, M.NAME as MPA_NAME
                FROM FILMS F
                JOIN MPA M ON M.MPA_ID = F.MPA_ID;
                """;
        Map<Long, Film> films = jdbc.query(getFilmsQuery, mapper).stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        // получить фильмы-жанры
        jdbc.query("SELECT * FROM FILMS_GENRES;", (rs, intRow) -> {
            Film film = null;
            while (rs.next()) {
                film = films.get(rs.getLong("FILM_ID"));
                Genre genre = genres.get(rs.getInt("GENRE_ID"));
                film.getGenres().add(genre);
            }
            return film;
        });
        // объединить в итоговую
        return films.values();
    }

    private Map<Integer, Genre> getIdsGenresMap() {
        return jdbc.query("SELECT * FROM GENRES;", new GenreMapper()).stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));
    }

    private Map<Integer, Mpa> getIdsMpaMap() {
        return jdbc.query("SELECT * FROM MPA;", new MpaMapper()).stream()
                .collect(Collectors.toMap(Mpa::getId, Function.identity()));
    }

    @Override
    public Optional<Film> getById(Long id) {
        try {
            String sqlQuery = """
                    SELECT f.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID, M.NAME as MPA_NAME
                    FROM FILMS F
                    JOIN MPA M ON M.MPA_ID = F.MPA_ID
                    WHERE FILM_ID = :filmId;
                    """;
            Film film = jdbc.queryForObject(sqlQuery, Map.of("filmId", id), mapper);
            Objects.requireNonNull(film);

            String getFilmGenresQuery = """
                    SELECT GENRE_ID, NAME
                        FROM GENRES
                        WHERE GENRE_ID IN (
                                    SELECT GENRE_ID
                                    FROM FILMS_GENRES
                                    WHERE FILM_ID = :filmId);
                        """;
            List<Genre> filmGenres = jdbc.query(getFilmGenresQuery, Map.of("filmId", id), new GenreMapper());
            film.getGenres().addAll(filmGenres);

            return Optional.of(film);
        } catch (NullPointerException | EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        String deleteFilmQuery = "DELETE FROM FILMS WHERE FILM_ID = :filmId;";
        jdbc.update(deleteFilmQuery,Map.of("filmId",id));
    }

    @Override
    public Collection<Film> getMostPopular(int count) {
        String sqlQuery = """
                SELECT
                    F.FILM_ID,
                    F.NAME,
                    F.DESCRIPTION,
                    F.RELEASE_DATE,
                    F.DURATION,
                    F.MPA_ID,
                    M.NAME as MPA_NAME,
                    COUNT(*) as total
                FROM FILMS F
                JOIN LIKES L ON L.FILM_ID = F.FILM_ID
                JOIN MPA M ON M.MPA_ID = F.MPA_ID
                GROUP BY F.FILM_ID
                ORDER BY total DESC, F.FILM_ID
                LIMIT :count;
                """;
        List<Film> films = jdbc.query(sqlQuery, Map.of("count", count), mapper);

        Map<Long, Film> idFilmsMap = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        Map<Integer, Genre> genres = getIdsGenresMap();

        jdbc.query("SELECT * FROM FILMS_GENRES WHERE FILM_ID IN (:filmsId);",
                Map.of("filmsId", idFilmsMap.keySet()),
                (rs, intRow) -> {
                    Film film = null;
                    while (rs.next()) {
                        film = idFilmsMap.get(rs.getLong("FILM_ID"));
                        Genre genre = genres.get(rs.getInt("GENRE_ID"));
                        film.getGenres().add(genre);
                    }
                    return film;
                });

        return films;
    }
}
