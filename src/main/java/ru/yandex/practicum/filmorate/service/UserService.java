package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface UserService {
    Collection<User> getAll();

    User getById(long userId);

    User create(User user);

    User update(User user);

    void delete(Long userId);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(long userId, long otherId);

    Collection<Film> getFilmRecommendations(long userId);

    Collection<Event> getFeed(long userId);
}
