package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public Collection<Genre> getAll() {
        return genreRepository.getAll();
    }

    @Override
    public Genre getById(int genreId) {
        Genre genre = genreRepository.getById(genreId)
                .orElseThrow(() -> {
                    log.debug("GET GENRE By ID {}. Рейтинг с айди {} не найден", genreId, genreId);
                    return new NotFoundException("Рейтинг с id=" + genreId + " не существует");
                });
        return genre;
    }
}
