package ru.yandex.practicum.filmorate.repository.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorRepository {
    Collection<Director> getAll();

    Optional<Director> getById(int directorId);

    Director create(Director director);

    Director update(Director director);

    void removeById(int directorId);
}
