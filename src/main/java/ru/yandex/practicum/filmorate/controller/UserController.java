package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        log.info("GET /users request");
        Collection<User> users = userService.getAll();
        log.info("GET /users response: {}", users.size());
        return users;
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) {
        log.info("GET /users/{} by ID {} request", userId, userId);
        User user = userService.getById(userId);
        log.info("GET /users/{} response: success {}", userId, user);
        return user;
    }

    @PostMapping
    public User create(@Validated @RequestBody User user) {
        log.info("POST /users request: {}", user);
        User createdUser = userService.create(user);
        log.info("POST /users response: {}", createdUser);
        return createdUser;
    }

    @PutMapping
    public User update(@Validated @RequestBody User user) {
        log.info("PUT /users request: {}", user);
        User updateUser = userService.update(user);
        log.info("PUT /users response: {}", updateUser);
        return updateUser;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("DELETE /users/{} request", id);
        userService.delete(id);
        log.info("DELETE /users/{} response: success", id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("PUT /users/{}/friends/{} request", id, friendId);
        userService.addFriend(id, friendId);
        log.info("PUT /users/{}/friends/{} response: success", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("DELETE /users/{}/friends/{} request", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("DELETE /users/{}/friends/{} response: success", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.info("GET /users/{}/friends request", id);
        Collection<User> userFriends = userService.getFriends(id);
        log.info("GET /users/{}/friends response: {}", id, userFriends);
        return userFriends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("GET /users/{}/friends/common/{} request", id, otherId);
        Collection<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("GET /users/{}/friends/common/{} response: {}", id, otherId, commonFriends);
        return commonFriends;
    }
}