package ru.yandex.practicum.filmorate.controller;

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
    public Collection<Film> getMostPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("GET /films/popular?count={} request", count);
        Collection<Film> films = filmService.getMostPopular(count);
        log.info("GET /films/popular?count={} response: {}", count, films.size());
        return films;
    }
}