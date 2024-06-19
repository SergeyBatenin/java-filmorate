package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    void like(long filmId, long userId);

    void unlike(long filmId, long userId);

    Collection<Film> getMostPopular(int count);

    Film getById(long filmId);

    Collection<Film> getCommonFilms(long userId, long friendId);
}