package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public Collection<User> getAll() {
        return repository.getAll();
    }

    @Override
    public User getById(long userId) {
        return repository.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
    }

    @Override
    public User create(User user) {
        checkAndInitializeUserName(user);
        return repository.create(user);
    }

    @Override
    public User update(User user) {
        final User updatedUser = repository.getById(user.getId())
                .orElseThrow(() -> {
                    log.debug("UPDATE {}. Пользователь с айди {} не найден", user, user.getId());
                    return new NotFoundException("Пользователь с id=" + user.getId() + " не существует");
                });
        checkAndInitializeUserName(user);
        return repository.update(user);
    }

    @Override
    public void delete(Long userId) {
        repository.delete(userId);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        final User user = repository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("ADD-FRIEND {}<->{}. Пользователь с айди {} не найден", userId, friendId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
        final User friend = repository.getById(friendId)
                .orElseThrow(() -> {
                    log.debug("ADD-FRIEND {}<->{}. Пользователь с айди {} не найден", userId, friendId, friendId);
                    return new NotFoundException("Пользователь с id=" + friendId + " не существует");
                });
        repository.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        User user = repository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("ADD-FRIEND {}<->{}. Пользователь с айди {} не найден", userId, friendId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
        User friend = repository.getById(friendId)
                .orElseThrow(() -> {
                    log.debug("ADD-FRIEND {}<->{}. Пользователь с айди {} не найден", userId, friendId, friendId);
                    return new NotFoundException("Пользователь с id=" + friendId + " не существует");
                });
        repository.deleteFriend(userId, friendId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        final User user = repository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("GET-FRIENDS {}. Пользователь с айди {} не найден", userId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
        return repository.getFriends(userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        final User user = repository.getById(userId)
                .orElseThrow(() -> {
                    log.info("GET-COMMON-FRIENDS {}<->{}. Пользователь с айди {} не найден", userId, otherId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
        final User other = repository.getById(otherId)
                .orElseThrow(() -> {
                    log.info("GET-COMMON-FRIENDS {}<->{}. Пользователь с айди {} не найден", userId, otherId, otherId);
                    return new NotFoundException("Пользователь с id=" + otherId + " не существует");
                });
        return repository.getCommonFriends(userId, otherId);
    }

    private void checkAndInitializeUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
