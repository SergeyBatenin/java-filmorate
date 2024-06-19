package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class InMemoryFilmRepository implements FilmRepository {
    private static long identifierFilm = 1;
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(identifierFilm++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Optional<Film> getById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void delete(Long id) {

    }

    public void like(long filmId, long userId) {
        Set<Long> filmLikes = likes.computeIfAbsent(filmId, id -> new HashSet<>());
        filmLikes.add(userId);
    }

    public void unlike(long filmId, long userId) {
        Set<Long> filmLikes = likes.computeIfAbsent(filmId, id -> new HashSet<>());
        filmLikes.remove(userId);
    }

    @Override
    public Collection<Film> getMostPopular(int count) {
        return films.values().stream()
                .filter(film -> likes.get(film.getId()) != null)
                .sorted((f1, f2) -> {
                    int likes1 = likes.get(f1.getId()).size();
                    int likes2 = likes.get(f2.getId()).size();
                    return  likes2 - likes1;
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}