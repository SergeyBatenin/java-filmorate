package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getAll() {
        log.info("GET /directors request");
        Collection<Director> directors = directorService.getAll();
        log.info("GET /directors response: {}", directors.size());
        return directors;
    }

    @GetMapping("/{directorId}")
    public Director getById(@PathVariable int directorId) {
        log.info("GET /directors/{} request", directorId);
        Director director = directorService.getById(directorId);
        log.info("GET /directors/{} response: {}", directorId, director);
        return director;
    }

    @PostMapping
    public Director create(@Validated @RequestBody Director director) {
        log.info("POST /directors request: {}", director);
        Director createdDirector = directorService.create(director);
        log.info("POST /directors response: {}", createdDirector);
        return createdDirector;
    }

    @PutMapping
    public Director update(@Validated @RequestBody Director director) {
        log.info("PUT /directors request: {}", director);
        Director updatedDirector = directorService.update(director);
        log.info("PUT /directors response: {}", updatedDirector);
        return updatedDirector;
    }

    @DeleteMapping("/{directorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable int directorId) {
        log.info("DELETE /directors/{} request", directorId);
        directorService.removeById(directorId);
        log.info("DELETE /directors/{} response", directorId);
    }
}