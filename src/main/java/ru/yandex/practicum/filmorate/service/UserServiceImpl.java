package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public Collection<User> getAll() {
        return repository.getAll();
    }

    public User create(User user) {
        checkAndInitializeUserName(user);
        final User createdUser = repository.create(user);
        return createdUser;
    }

    public User update(User user) {
        final User updatedUser = repository.getById(user.getId());
        if (updatedUser == null) {
            log.warn("UPDATE {}. Пользователь с айди {} не найден", user, user.getId());
            throw new NotFoundException("Пользователь с таким айди не существует");
        }
        checkAndInitializeUserName(user);
        return repository.update(user);
    }

    private void checkAndInitializeUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
