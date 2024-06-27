package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    void delete(long filmId);

    void like(long filmId, long userId);

    void unlike(long filmId, long userId);

    Collection<Film> getMostPopular(Integer count, Integer genreId, Integer year);

    Film getById(long filmId);

    Collection<Film> getByDirector(int directorId, String sortBy);

    Collection<Film> getCommonFilms(long userId, long friendId);

    Collection<Film> search(String keyword, String params);
}