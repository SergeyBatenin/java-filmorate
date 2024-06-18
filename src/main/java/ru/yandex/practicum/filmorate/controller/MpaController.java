package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> getAll() {
        log.info("GET /mpa request");
        Collection<Mpa> mpas = mpaService.getAll();
        log.info("GET /mpa response: {}", mpas.size());
        return mpas;
    }

    @GetMapping("/{mpaId}")
    public Mpa getById(@PathVariable int mpaId) {
        log.info("GET /mpa/{} request", mpaId);
        Mpa mpa = mpaService.getById(mpaId);
        log.info("GET /mpa/{} response: {}", mpaId, mpa);
        return mpa;
    }
}
