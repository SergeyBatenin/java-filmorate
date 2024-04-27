package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

public class UserService {
    private final UserRepository repository = new UserRepository();

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
