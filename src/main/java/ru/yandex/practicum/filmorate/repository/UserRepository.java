package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {
    private static long identifierUser = 1;
    private final Map<Long, User> users = new HashMap<>();

    public User create(User user) {
        user.setId(identifierUser++);
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public Collection<User> getAll() {
        return users.values();
    }

    public User getById(Long id) {
        return users.get(id);
    }
}
