package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Film create(Film film) {
        return filmRepository.create(film);
    }

    @Override
    public Film update(Film film) {
        final Film updatedFilm = filmRepository.getById(film.getId())
                .orElseThrow(() -> {
                    log.warn("UPDATE {}. Фильм с id={} не найден", film, film.getId());
                    return new NotFoundException("Фильм с id=" + film.getId() + " не существует");
                });
        return filmRepository.update(film);
    }

    @Override
    public void like(long filmId, long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.warn("LIKE-FILM {}<-{}. Пользователь с id={} не найден", filmId, userId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
        filmRepository.getById(filmId)
                .orElseThrow(() -> {
                    log.warn("LIKE-FILM {}<-{}. Фильм с id={} не найден", filmId, userId, filmId);
                    return new NotFoundException("Фильм с id=" + filmId + " не существует");
                });
        filmRepository.like(filmId, userId);
    }

    @Override
    public void unlike(long filmId, long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.warn("LIKE-FILM {}<-{}. Пользователь с id={} не найден", filmId, userId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
        filmRepository.getById(filmId)
                .orElseThrow(() -> {
                    log.warn("LIKE-FILM {}<-{}. Фильм с id={} не найден", filmId, userId, filmId);
                    return new NotFoundException("Фильм с id=" + filmId + " не существует");
                });
        filmRepository.unlike(filmId, userId);
    }

    @Override
    public Collection<Film> getMostPopular(int count) {
        return filmRepository.getMostPopular(count);
    }
}