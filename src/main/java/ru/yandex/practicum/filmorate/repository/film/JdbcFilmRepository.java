package ru.yandex.practicum.filmorate.repository.film;

import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.SaveDataException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.BaseJdbcRepository;
import ru.yandex.practicum.filmorate.repository.director.DirectorMapper;
import ru.yandex.practicum.filmorate.repository.genre.GenreMapper;
import ru.yandex.practicum.filmorate.repository.mpa.MpaMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
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
        insertFilmDirectors(film);

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

        String removeFilmDirectorsQuery = "DELETE FROM FILMS_DIRECTORS WHERE FILM_ID = :filmId";
        jdbc.update(removeFilmDirectorsQuery, Map.of("filmId", film.getId()));

        insertFilmGenres(film);
        insertFilmDirectors(film);
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

    private void insertFilmDirectors(Film film) {
        Set<Director> directors = film.getDirectors();
        if (directors == null || directors.isEmpty()) {
            return;
        }

        String insertFilmDirectorsQuery = "INSERT INTO FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) " +
                "VALUES (:filmId, :directorId);";
        List<MapSqlParameterSource> directorParams = directors.stream()
                .map(director -> new MapSqlParameterSource()
                        .addValue("filmId", film.getId())
                        .addValue("directorId", director.getId()))
                .toList();
        jdbc.batchUpdate(insertFilmDirectorsQuery, directorParams.toArray(new MapSqlParameterSource[0]));
    }

    @Override
    public Collection<Film> getAll() {
        // получить фильмы
        String getFilmsQuery = """
                SELECT F.FilM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID, M.NAME as MPA_NAME
                FROM FILMS F
                JOIN MPA M ON M.MPA_ID = F.MPA_ID;
                """;
        Map<Long, Film> films = jdbc.query(getFilmsQuery, mapper).stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        initializeGenresAndDirectors(films);

        return films.values();
    }

    private Map<Integer, Director> getIdsDirectorsMap() {
        return jdbc.query("SELECT * FROM DIRECTORS;", new DirectorMapper()).stream()
                .collect(Collectors.toMap(Director::getId, Function.identity()));
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
    public Collection<Film> getByDirector(int directorId, String sortBy) {
        LinkedHashMap<Long, Film> films;
        if ("year".equals(sortBy)) {
            films = getByYear(directorId);
        } else {
            films = getByLikes(directorId);
        }

        initializeGenresAndDirectors(films);

        return films.values();
    }

    private void initializeGenresAndDirectors(Map<Long, Film> films) {
        // получить все жанры
        Map<Integer, Genre> genres = getIdsGenresMap();
        // получить всех режиссеров
        Map<Integer, Director> directors = getIdsDirectorsMap();
        // получить фильмы-жанры
        jdbc.query("SELECT * FROM FILMS_GENRES;", new ResultSetExtractor<Film>() {
            @Override
            public Film extractData(@NonNull ResultSet rs) throws SQLException, DataAccessException {

                Film film = null;
                while (rs.next()) {
                    film = films.get(rs.getLong("FILM_ID"));
                    if (film != null) {
                        Genre genre = genres.get(rs.getInt("GENRE_ID"));
                        film.getGenres().add(genre);
                    }
                }
                return film;
            }
        });
        // получить режиссеров фильмов
        jdbc.query("SELECT * FROM FILMS_DIRECTORS;", new ResultSetExtractor<Film>() {
            @Override
            public Film extractData(@NonNull ResultSet rs) throws SQLException, DataAccessException {

                Film film = null;
                while (rs.next()) {
                    film = films.get(rs.getLong("FILM_ID"));
                    if (film != null) {
                        Director director = directors.get(rs.getInt("DIRECTOR_ID"));
                        film.getDirectors().add(director);
                    }
                }
                return film;
            }
        });
    }

    private LinkedHashMap<Long, Film> getByYear(int directorId) {
        String getFilmsByYear = """
                SELECT
                    F.FILM_ID,
                    F.NAME,
                    F.DESCRIPTION,
                    F.RELEASE_DATE,
                    F.DURATION,
                    M.MPA_ID,
                    M.NAME as MPA_NAME
                FROM FILMS F
                JOIN MPA M ON M.MPA_ID = F.MPA_ID
                JOIN FILMS_DIRECTORS FD ON FD.FILM_ID = F.FILM_ID
                WHERE FD.DIRECTOR_ID = :directorId
                GROUP BY F.FILM_ID
                ORDER BY F.RELEASE_DATE""";
        return jdbc.query(getFilmsByYear, Map.of("directorId", directorId), mapper).stream()
                .collect(Collectors.toMap(Film::getId, Function.identity(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }

    private LinkedHashMap<Long, Film> getByLikes(int directorId) {
        String getFilmsByLikes = """
                SELECT
                    F.FILM_ID,
                    F.NAME,
                    F.DESCRIPTION,
                    F.RELEASE_DATE,
                    F.DURATION,
                    M.MPA_ID,
                    M.NAME as MPA_NAME,
                    COUNT(L.FILM_ID) as LIKES
                FROM FILMS F
                JOIN MPA M ON M.MPA_ID = F.MPA_ID
                JOIN FILMS_DIRECTORS FD ON FD.FILM_ID = F.FILM_ID
                LEFT JOIN LIKES L ON L.FILM_ID = F.FILM_ID
                WHERE FD.DIRECTOR_ID = :directorId
                GROUP BY F.FILM_ID
                ORDER BY LIKES DESC""";
        return jdbc.query(getFilmsByLikes, Map.of("directorId", directorId), mapper).stream()
                .collect(Collectors.toMap(Film::getId, Function.identity(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
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
                    SELECT G.GENRE_ID, G.NAME
                    FROM FILMS_GENRES FG
                    JOIN GENRES G ON G.GENRE_ID = FG.GENRE_ID
                    WHERE FILM_ID = :filmId;
                    """;
            List<Genre> filmGenres = jdbc.query(getFilmGenresQuery, Map.of("filmId", id), new GenreMapper());
            film.getGenres().addAll(filmGenres);

            String getFilmDirectorsQuery = """
                    SELECT D.DIRECTOR_ID, D.NAME
                    FROM FILMS_DIRECTORS FD
                    JOIN DIRECTORS D ON D.DIRECTOR_ID = FD.DIRECTOR_ID
                    WHERE FILM_ID = :filmId;
                    """;
            List<Director> filmDirectors = jdbc.query(
                    getFilmDirectorsQuery,
                    Map.of("filmId", id),
                    new DirectorMapper());
            film.getDirectors().addAll(filmDirectors);

            return Optional.of(film);
        } catch (NullPointerException | EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        String deleteFilmQuery = "DELETE FROM FILMS WHERE FILM_ID = :filmId;";
        jdbc.update(deleteFilmQuery, Map.of("filmId", id));
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
                    COUNT(L.FILM_ID) AS LIKE_COUNT
                FROM FILMS F
                LEFT JOIN LIKES L ON L.FILM_ID = F.FILM_ID
                JOIN MPA M ON M.MPA_ID = F.MPA_ID
                GROUP BY F.FILM_ID
                ORDER BY LIKE_COUNT DESC, F.FILM_ID
                LIMIT :count;
                """;
        LinkedHashMap<Long, Film> films = jdbc.query(sqlQuery, Map.of("count", count), mapper).stream()
                .collect(Collectors.toMap(Film::getId, Function.identity(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        initializeGenresAndDirectors(films);

        return films.values();
    }

    @Override
    public Collection<Film> getPopularFilmsByYear(int year) {
        String sqlQuery = """
                SELECT
                    F.FILM_ID AS ID,
                    F.NAME AS FILM_NAME,
                    F.DESCRIPTION,
                    F.RELEASE_DATE,
                    F.DURATION,
                    F.MPA_ID,
                    M.NAME as MPA_NAME,
                    COUNT(L.FILM_ID) AS LIKE_COUNT
                FROM FILMS F
                JOIN FILMS_GENRES FG ON F.FILM_ID = FG.FILM_ID
                LEFT JOIN GENRES G ON FG.GENRE_ID = G.GENRE_ID
                JOIN MPA M ON F.MPA_ID = M.MPA_ID
                LEFT JOIN LIKES L ON L.FILM_ID = F.FILM_ID
                WHERE YEAR(F.RELEASE_DATE) = :year
                GROUP BY F.FILM_ID, FG.GENRE_ID
                ORDER BY LIKE_COUNT DESC;
                """;
        LinkedHashMap<Long, Film> films = jdbc.query(sqlQuery, Map.of("year", year), mapper).stream()
                .collect(Collectors.toMap(Film::getId, Function.identity(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        initializeGenresAndDirectors(films);

        return films.values();
    }

    @Override
    public Collection<Film> getPopularFilmsByGenre(int genreId) {
        String sqlQuery = """
                SELECT
                    F.FILM_ID AS ID,
                    F.NAME AS FILM_NAME,
                    F.DESCRIPTION,
                    F.RELEASE_DATE,
                    F.DURATION,
                    F.MPA_ID,
                    M.NAME as MPA_NAME,
                    COUNT(L.FILM_ID) AS LIKE_COUNT
                FROM FILMS F
                JOIN FILMS_GENRES FG ON F.FILM_ID = FG.FILM_ID
                LEFT JOIN GENRES G ON FG.GENRE_ID = G.GENRE_ID
                JOIN MPA M ON F.MPA_ID = M.MPA_ID
                LEFT JOIN LIKES L ON L.FILM_ID = F.FILM_ID
                WHERE G.GENRE_ID = :genreId
                GROUP BY F.FILM_ID, FG.GENRE_ID
                ORDER BY LIKE_COUNT DESC;
                """;
        LinkedHashMap<Long, Film> films = jdbc.query(sqlQuery, Map.of("genreId", genreId), mapper).stream()
                .collect(Collectors.toMap(Film::getId, Function.identity(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        initializeGenresAndDirectors(films);

        return films.values();
    }

    @Override
    public Collection<Film> getPopularFilmsByYearAndGenre(int year, int genreId) {
        String sqlQuery = """
                SELECT
                    F.FILM_ID AS ID,
                    F.NAME AS FILM_NAME,
                    F.DESCRIPTION,
                    F.RELEASE_DATE,
                    F.DURATION,
                    F.MPA_ID,
                    M.NAME as MPA_NAME,
                    COUNT(L.FILM_ID) AS LIKE_COUNT
                FROM FILMS F
                JOIN FILMS_GENRES FG ON F.FILM_ID = FG.FILM_ID
                LEFT JOIN GENRES G ON FG.GENRE_ID = G.GENRE_ID
                JOIN MPA M ON F.MPA_ID = M.MPA_ID
                LEFT JOIN LIKES L ON L.FILM_ID = F.FILM_ID
                WHERE YEAR(F.RELEASE_DATE) = :year AND G.GENRE_ID = :genreId
                GROUP BY F.FILM_ID, FG.GENRE_ID
                ORDER BY LIKE_COUNT DESC;
                """;
        LinkedHashMap<Long, Film> films = jdbc.query(sqlQuery, Map.of("year",year,"genreId", genreId), mapper).stream()
                .collect(Collectors.toMap(Film::getId, Function.identity(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        initializeGenresAndDirectors(films);

        return films.values();
    }

    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        String sqlQuery = """
                SELECT
                    F.FilM_ID,
                    F.NAME,
                    F.DESCRIPTION,
                    F.RELEASE_DATE,
                    F.DURATION,
                    F.MPA_ID,
                    M.NAME as MPA_NAME,
                    COUNT(L_ALL.USER_ID) as LIKES
                FROM FILMS F
                JOIN LIKES L1 ON L1.FILM_ID = F.FILM_ID
                JOIN LIKES L2 ON L2.FILM_ID = F.FILM_ID
                LEFT JOIN LIKES L_ALL ON L_ALL.FILM_ID = F.FILM_ID
                LEFT JOIN MPA M ON M.MPA_ID = F.MPA_ID
                WHERE L1.USER_ID = :userId AND L2.USER_ID = :friendId
                GROUP BY F.FILM_ID, M.MPA_ID
                ORDER BY LIKES DESC, F.FILM_ID;
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId);

        Map<Long, Film> filmMap = jdbc.query(sqlQuery, params, mapper).stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        Map<Integer, Genre> genres = getIdsGenresMap();

        jdbc.query("SELECT * FROM FILMS_GENRES WHERE FILM_ID IN (:filmIds);",
                Map.of("filmIds", filmMap.keySet()),
                (rs, rowNum) -> {
                    Film film = filmMap.get(rs.getLong("FILM_ID"));
                    Genre genre = genres.get(rs.getInt("GENRE_ID"));
                    film.getGenres().add(genre);
                    return film;
                });

        return filmMap.values();
    }

    @Override
    public Collection<Film> search(String keyword, Set<String> searchParams) {
        String searchQueryPattern = """
                SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID, M.NAME as MPA_NAME
                    FROM FILMS F
                    JOIN MPA M ON M.MPA_ID = F.MPA_ID
                    LEFT JOIN FILMS_DIRECTORS FD ON FD.FILM_ID = F.FILM_ID
                    LEFT JOIN DIRECTORS D ON D.DIRECTOR_ID = FD.DIRECTOR_ID
                    WHERE%s;""";

        String whereCondition = "";
        if (searchParams.contains("title")) {
            whereCondition = " F.NAME ILIKE '%" + keyword + "%'";
            if (searchParams.size() == 2) {
                whereCondition += " OR";
            }
        }
        if (searchParams.contains("director")) {
            whereCondition += " D.NAME ILIKE '%" + keyword + "%'";
        }
        String searchQuery = String.format(searchQueryPattern, whereCondition);

        Map<Long, Film> films = jdbc.query(searchQuery, mapper).stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        initializeGenresAndDirectors(films);
        return films.values();
    }
}
