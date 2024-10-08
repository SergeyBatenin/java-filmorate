package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.feed.FeedRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.like.LikeRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final FeedRepository feedRepository;

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
                    return new NotFoundException("Фильм с id=" + filmId + " не существует");
                });
    }

    @Override
    public Film create(Film film) {
        checkFilmMpa(film);
        checkFilmGenres(film);
        checkFilmDirectors(film);

        return filmRepository.create(film);
    }

    @Override
    public Film update(Film film) {
        checkFilmExist(film.getId(), "UPDATE");
        checkFilmMpa(film);
        checkFilmGenres(film);
        checkFilmDirectors(film);

        return filmRepository.update(film);
    }

    @Override
    public void delete(long filmId) {
        filmRepository.delete(filmId);
    }

    @Override
    public void like(long filmId, long userId) {
        checkUserExist(userId, "LIKE-FILM");
        checkFilmExist(filmId, "LIKE-FILM");

        likeRepository.like(filmId, userId);
        feedRepository.saveEvent(userId, Operation.ADD, EventType.LIKE, filmId);
    }

    @Override
    public void unlike(long filmId, long userId) {
        checkUserExist(userId, "UNLIKE-FILM");
        checkFilmExist(filmId, "UNLIKE-FILM");

        likeRepository.unlike(filmId, userId);
        feedRepository.saveEvent(userId, Operation.REMOVE, EventType.LIKE, filmId);
    }

    public Collection<Film> getMostPopular(Integer count, Integer genreId, Integer year) {
        Collection<Film> films;
        if (genreId == null && year == null) {
            films = filmRepository.getMostPopular(count);
        } else if (genreId == null) {
            films = filmRepository.getPopularFilmsByYear(year);
        } else if (year == null) {
            genreRepository.getById(genreId).orElseThrow(() -> {
                log.info("GET-MOST-POPULAR. Жанр с id={} не найден", genreId);
                return new NotFoundException("Жанр с id=" + genreId + " не существует");
            });
            films = filmRepository.getPopularFilmsByGenre(genreId);
        } else {
            films = filmRepository.getPopularFilmsByYearAndGenre(year, genreId);
        }
        return films;
    }

    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        checkUserExist(userId, "COMMON-FILM-USER");
        checkUserExist(friendId, "COMMON-FILM-FRIEND");

        return filmRepository.getCommonFilms(userId, friendId);
    }

    @Override
    public Collection<Film> search(String keyword, String params) {
        Set<String> searchParams = Arrays.stream(params.split(",")).collect(Collectors.toSet());
        return filmRepository.search(keyword.toLowerCase(), searchParams);
    }

    private void checkFilmMpa(Film film) {
        int mapId = film.getMpa().getId();
        mpaRepository.getById(mapId)
                .orElseThrow(() -> {
                    log.debug("CHECK MpaFilm {}. Рейтинг с id={} не найден", film, mapId);
                    return new IllegalArgumentException("Рейтинг с id=" + mapId + " не существует");
                });
    }

    private void checkFilmGenres(Film film) {
        Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }

        List<Integer> genreIds = genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toList());

        int matchingGenresCount = genreRepository.countMatchingGenres(genreIds);

        if (matchingGenresCount != genres.size()) {
            log.debug("CHECK FilmGenres {}. Обнаружен несуществующий жанр в списке {}", film, genres);
            throw new IllegalArgumentException("Фильм содержит несуществующий жанр");
        }
    }

    private void checkFilmDirectors(Film film) {
        Set<Director> directors = film.getDirectors();
        if (directors == null || directors.isEmpty()) {
            return;
        }

        List<Integer> directorIds = directors.stream()
                .map(Director::getId)
                .collect(Collectors.toList());

        int matchingDirectorsCount = directorRepository.countMatchingDirectors(directorIds);
        if (matchingDirectorsCount != directors.size()) {
            log.debug("CHECK FilmDirectors {}. Обнаружен несуществующий режиссер в списке {}", film, directors);
            throw new IllegalArgumentException("Фильм содержит не существующего режиссера");
        }
    }

    private void checkFilmExist(Long filmId, String method) {
        filmRepository.getById(filmId).orElseThrow(() -> {
            log.debug("{}. Фильм с id={} не найден", method, filmId);
            return new NotFoundException("Фильм с id=" + filmId + " не существует");
        });
    }

    private void checkUserExist(Long userId, String method) {
        userRepository.getById(userId).orElseThrow(() -> {
            log.info("{}. Пользователь с id={} не найден", method, userId);
            return new NotFoundException("Пользователь с id=" + userId + " не существует");
        });
    }
}