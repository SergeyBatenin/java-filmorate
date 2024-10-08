package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Validated
@RequiredArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        log.info("GET /films request");
        Collection<Film> films = filmService.getAll();
        log.info("GET /films response: {}", films.size());
        return films;
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        log.info("GET /films/director/{}/{} request", directorId, sortBy);
        Collection<Film> films = filmService.getByDirector(directorId, sortBy);
        log.info("GET /films/director/{}/{} response: {}", directorId, sortBy, films.size());
        return films;
    }

    @GetMapping("/{filmId}")
    public Film getById(@PathVariable long filmId) {
        log.info("GET /films{} request", filmId);
        Film film = filmService.getById(filmId);
        log.info("GET /films{} response: {}", filmId, film);
        return film;
    }


    @PostMapping
    public Film create(@Validated @RequestBody Film film) {
        log.info("POST /films request: {}", film);
        Film createdFilm = filmService.create(film);
        log.info("POST /films response: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@Validated @RequestBody Film film) {
        log.info("PUT /films request: {}", film);
        Film updatedFilm = filmService.update(film);
        log.info("PUT /films response: {}", updatedFilm);
        return updatedFilm;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("DELETE /films/{} request", id);
        filmService.delete(id);
        log.info("DELETE /films/{} response: success", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable long id, @PathVariable long userId) {
        log.info("PUT /films/{}/like/{} request", id, userId);
        filmService.like(id, userId);
        log.info("PUT /films/{}/like/{} response: success", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlike(@PathVariable long id, @PathVariable long userId) {
        log.info("DELETE /films/{}/like/{} request", id, userId);
        filmService.unlike(id, userId);
        log.info("DELETE /films/{}/like/{} response: success", id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopular(@RequestParam(defaultValue = "10") @Min(0) Integer count,
                                           @RequestParam(required = false) Integer genreId,
                                           @RequestParam(required = false) @Min(1895) Integer year) {
        log.info("GET /films/popular?count={}, genreId={}, year={} request", count, genreId, year);
        Collection<Film> films = filmService.getMostPopular(count, genreId, year);
        log.info("GET /films/popular?count={}, genreId={}, year={} response: {} ", count, genreId, year, films.size());
        return films;
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        log.info("GET /films/common?userId={}, friendId={} request", userId, friendId);
        Collection<Film> films = filmService.getCommonFilms(userId, friendId);
        log.info("GET /films/common?userId={}, friendId={} response", userId, friendId);
        return films;
    }

    @GetMapping("/search")
    public Collection<Film> search(@RequestParam(name = "query") String keyword,
                                   @RequestParam(name = "by") String params) {
        log.info("GET /films/search?query={}, by={} request", keyword, params);
        Collection<Film> films = filmService.search(keyword, params);
        log.info("GET /films/search?query={}, by={} response: {}", keyword, params, films.size());
        return films;
    }
}