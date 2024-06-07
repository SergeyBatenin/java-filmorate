package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaServiceImpl implements MpaService {
    private final MpaRepository mpaRepository;

    @Override
    public Collection<Mpa> getAll() {
        return mpaRepository.getAll();
    }

    @Override
    public Mpa getById(int mpaId) {
        Mpa mpa = mpaRepository.getById(mpaId)
                .orElseThrow(() -> {
                    log.debug("GET MPA By ID {}. Рейтинг с айди {} не найден", mpaId, mpaId);
                    return new NotFoundException("Рейтинг с id=" + mpaId + " не существует");
                });
        return mpa;
    }
}
