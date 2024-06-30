package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {
    Collection<Director> getAll();

    Director getById(int directorId);

    Director create(Director director);

    Director update(Director director);

    void removeById(int directorId);
}