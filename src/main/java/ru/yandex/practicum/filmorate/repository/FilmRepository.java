package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmRepository {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> getAll();

    Film getById(Long id);
}
