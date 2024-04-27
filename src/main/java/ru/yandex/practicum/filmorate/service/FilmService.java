package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.Collection;

public class FilmService {
    private final FilmRepository repository = new FilmRepository();

    public Collection<Film> getAll() {
        return repository.getAll();
    }

    public Film create(Film film) {
        return repository.create(film);
    }

    public Film update(Film film) {
        Film updatedFilm = repository.getById(film.getId());
        if (updatedFilm == null) {
            throw new NotFoundException("Фильма с таким айди не существует");
        }
        return repository.update(film);
    }
}