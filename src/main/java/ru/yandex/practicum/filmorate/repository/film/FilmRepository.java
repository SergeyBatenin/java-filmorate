package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmRepository {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> getAll();

    Optional<Film> getById(Long id);

    Collection<Film> getByDirector(int directorId, String sortBy);

    void delete(Long id);

    Collection<Film> getMostPopular(int count);

    Collection<Film> getCommonFilms(long userId, long friendId);

    Collection<Film> search(String keyword, Set<String> searchParams);

    Collection<Film> getPopularFilmsByYear(int year);

    Collection<Film> getPopularFilmsByGenre(int genreId);

    Collection<Film> getPopularFilmsByYearAndGenre(int year, int genreId);
}
