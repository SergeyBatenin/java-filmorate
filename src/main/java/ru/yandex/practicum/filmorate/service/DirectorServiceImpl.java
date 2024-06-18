package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;

    @Override
    public Collection<Director> getAll() {
        return directorRepository.getAll();
    }

    @Override
    public Director getById(int directorId) {
        final Director director = directorRepository.getById(directorId)
                .orElseThrow(() -> {
                    log.debug("GET DIRECTOR By ID {}. Режиссер с айди {} не найден", directorId, directorId);
                    return new NotFoundException("Режиссер с id=" + directorId + " не существует");
                });
        return director;
    }

    @Override
    public Director create(Director director) {
        return directorRepository.create(director);
    }

    @Override
    public Director update(Director director) {
        final Director updatedDirector = directorRepository.getById(director.getId())
                .orElseThrow(() -> {
                    log.debug("GET DIRECTOR {}. Режиссер с айди {} не найден", director, director.getId());
                    return new NotFoundException("Режиссер с id=" + director.getId() + " не существует");
                });

        return directorRepository.update(director);
    }

    @Override
    public void removeById(int directorId) {
        directorRepository.removeById(directorId);
    }
}
