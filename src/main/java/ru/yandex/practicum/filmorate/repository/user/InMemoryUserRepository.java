package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static long identifierUser = 1;
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(identifierUser++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(long userId) {

    }

    @Override
    public void addFriend(long userId, long friendId) {
        final Set<Long> userFriendIds = friends.computeIfAbsent(userId, id -> new HashSet<>());
        final Set<Long> friendFriendIds = friends.computeIfAbsent(friendId, id -> new HashSet<>());
        userFriendIds.add(friendId);
        friendFriendIds.add(userId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        final Set<Long> userFriendIds = friends.computeIfAbsent(userId, id -> new HashSet<>());
        final Set<Long> friendFriendIds = friends.computeIfAbsent(friendId, id -> new HashSet<>());
        userFriendIds.remove(friendId);
        friendFriendIds.remove(userId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        final Set<Long> userFriendIds = friends.getOrDefault(userId, new HashSet<>());
        return userFriendIds.stream()
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        final Set<Long> userFriendIds = friends.computeIfAbsent(userId, id -> new HashSet<>());
        final Set<Long> friendFriendIds = friends.computeIfAbsent(otherId, id -> new HashSet<>());

        final Set<Long> commonFriendIds = new HashSet<>(userFriendIds);
        commonFriendIds.retainAll(friendFriendIds);

        return commonFriendIds.stream()
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<Film> getFilmRecommendations(long userId) {
        return Collections.emptyList();
    }
}
