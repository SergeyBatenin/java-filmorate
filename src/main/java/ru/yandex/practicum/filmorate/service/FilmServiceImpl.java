package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotExistsException;
import ru.yandex.practicum.filmorate.exception.MpaNotExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.like.LikeRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;
    private final LikeRepository likeRepository;
    private final DirectorRepository directorRepository;

    @Override
    public Collection<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Collection<Film> getByDirector(int directorId, String sortBy) {
        directorRepository.getById(directorId)
                .orElseThrow(() -> {
                    log.debug("GET DIRECTOR By ID {}. Режиссер с айди {} не найден", directorId, directorId);
                    return new NotFoundException("Режиссер с id=" + directorId + " не существует");
                });
        return filmRepository.getByDirector(directorId, sortBy);
    }

    @Override
    public Film getById(long filmId) {
        return filmRepository.getById(filmId)
                .orElseThrow(() -> {
                    log.debug("GET FILM By ID {}. Фильм с айди {} не найден", filmId, filmId);
                    return new NotFoundException("Фильма с id=" + filmId + " не существует");
                });
    }

    @Override
    public Film create(Film film) {
        checkFilmMpa(film);
        checkFilmGenres(film);

        return filmRepository.create(film);
    }

    @Override
    public Film update(Film film) {
        filmRepository.getById(film.getId())
                .orElseThrow(() -> {
                    log.debug("UPDATE {}. Фильм с id={} не найден", film, film.getId());
                    return new NotFoundException("Фильма с id=" + film.getId() + " не существует");
                });
        checkFilmMpa(film);
        checkFilmGenres(film);

        return filmRepository.update(film);
    }

    @Override
    public void delete(long filmId) {
        filmRepository.delete(filmId);
    }

    private void checkFilmMpa(Film film) {
        int mapId = film.getMpa().getId();
        mpaRepository.getById(mapId)
                .orElseThrow(() -> {
                    log.debug("CHECK MpaFilm {}. Рейтинг с id={} не найден", film, mapId);
                    return new MpaNotExistsException("Рейтинга с id=" + mapId + " не существует");
                });
    }

    private void checkFilmGenres(Film film) {
        Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }

        Collection<Genre> allGenres = genreRepository.getAll();
        boolean isContains = allGenres.containsAll(genres);
        if (!isContains) {
            log.debug("CHECK FilmGenres {}. Обнаружен несуществующий жанр в списке {}", film, allGenres);
            throw new GenreNotExistsException("Фильм содержит не существующий жанр");
        }
    }

    @Override
    public void like(long filmId, long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("LIKE-FILM {}<-{}. Пользователь с id={} не найден", filmId, userId, userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        filmRepository.getById(filmId)
                .orElseThrow(() -> {
                    log.debug("LIKE-FILM {}<-{}. Фильм с id={} не найден", filmId, userId, filmId);
                    return new NotFoundException("Фильма с id=" + filmId + " не существует");
                });
        likeRepository.like(filmId, userId);
    }

    @Override
    public void unlike(long filmId, long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("UNLIKE-FILM {}<-{}. Пользователь с id={} не найден", filmId, userId, userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        filmRepository.getById(filmId)
                .orElseThrow(() -> {
                    log.debug("UNLIKE-FILM {}<-{}. Фильм с id={} не найден", filmId, userId, filmId);
                    return new NotFoundException("Фильма с id=" + filmId + " не существует");
                });
        likeRepository.unlike(filmId, userId);
    }

    @Override
    public Collection<Film> getMostPopular(int count) {
        return filmRepository.getMostPopular(count);
    }

    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("FILM-COMMON {}<-{}. Пользователь с id={} не найден", userId, friendId, userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        userRepository.getById(friendId)
                .orElseThrow(() -> {
                    log.debug("FILM-COMMON {}<-{}. Пользователь с id={} не найден", userId, friendId, friendId);
                    return new NotFoundException("Пользователя с id=" + friendId + " не существует");
        });
        return filmRepository.getCommonFilms(userId, friendId);
    }
}