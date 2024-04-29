package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class FilmRepository {
    private static long identifierFilm = 1;
    private final Map<Long, Film> films = new HashMap<>();

    public Film create(Film film) {
        film.setId(identifierFilm++);
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    public Collection<Film> getAll() {
        return films.values();
    }

    public Film getById(Long id) {
        return films.get(id);
    }
}
