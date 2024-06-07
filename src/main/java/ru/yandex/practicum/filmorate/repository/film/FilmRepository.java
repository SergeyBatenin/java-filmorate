package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmRepository {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> getAll();

    Optional<Film> getById(Long id);

    Collection<Film> getMostPopular(int count);
}
