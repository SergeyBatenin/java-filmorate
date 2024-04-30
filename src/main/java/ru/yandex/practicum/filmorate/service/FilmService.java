package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmRepository repository;

    public Collection<Film> getAll() {
        return repository.getAll();
    }

    public Film create(Film film) {
        return repository.create(film);
    }

    public Film update(Film film) {
        Film updatedFilm = repository.getById(film.getId());
        if (updatedFilm == null) {
            log.warn("UPDATE {}. Фильм с айди {} не найден", film, film.getId());
            throw new NotFoundException("Фильма с таким айди не существует");
        }
        return repository.update(film);
    }
}