package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAll() {
        log.info("GET /genres request");
        Collection<Genre> genres = genreService.getAll();
        log.info("GET /genres response: {}", genres.size());
        return genres;
    }

    @GetMapping("/{genreId}")
    public Genre getById(@PathVariable int genreId) {
        log.info("GET /genres/{} request", genreId);
        Genre genre = genreService.getById(genreId);
        log.info("GET /genres/{} response: {}", genreId, genre);
        return genre;
    }
}