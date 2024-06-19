package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    User update(User user);

    Collection<User> getAll();

    Optional<User> getById(Long id);

    void delete(long userId);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(long userId, long otherId);
}
