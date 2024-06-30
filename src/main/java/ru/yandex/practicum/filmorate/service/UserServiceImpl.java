package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.feed.FeedRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final FilmRepository filmRepository;
    private final FeedRepository feedRepository;

    @Override
    public Collection<User> getAll() {
        return repository.getAll();
    }

    @Override
    public User getById(long userId) {
        return repository.getById(userId)
                .orElseThrow(() -> {
                    log.info("GET Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
    }

    @Override
    public User create(User user) {
        checkAndInitializeUserName(user);
        return repository.create(user);
    }

    @Override
    public User update(User user) {
        checkUserExistence(user.getId(), "UPDATE");
        checkAndInitializeUserName(user);
        return repository.update(user);
    }

    @Override
    public void delete(Long userId) {
        repository.delete(userId);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        checkUserExistence(userId, "ADD-FRIEND-USER");
        checkUserExistence(friendId, "ADD-FRIEND-FRIEND");
        repository.addFriend(userId, friendId);
        feedRepository.saveEvent(userId, Operation.ADD, EventType.FRIEND, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        checkUserExistence(userId, "DELETE-FRIEND-USER");
        checkUserExistence(friendId, "DELETE-FRIEND-FRIEND");
        repository.deleteFriend(userId, friendId);
        feedRepository.saveEvent(userId, Operation.REMOVE, EventType.FRIEND, friendId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        checkUserExistence(userId, "GET-FRIENDS");
        return repository.getFriends(userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        checkUserExistence(userId, "GET-COMMON-FRIENDS-USER");
        checkUserExistence(otherId, "GET-COMMON-FRIENDS-FRIEND");
        return repository.getCommonFriends(userId, otherId);
    }

    @Override
    public Collection<Film> getFilmRecommendations(long userId) {
        checkUserExistence(userId, "FILM-RECOMMENDATIONS");
        return filmRepository.getFilmRecommendations(userId);
    }

    @Override
    public Collection<Event> getFeed(long userId) {
        checkUserExistence(userId, "GET-FEED");
        return repository.getFeed(userId);
    }

    private void checkAndInitializeUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkUserExistence(Long userId, String method) {
        repository.getById(userId)
                .orElseThrow(() -> {
                    log.info("{} Пользователь с id={} не найден", method, userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
    }
}
