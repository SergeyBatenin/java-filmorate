package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
        log.info("GET /films response: {}", users.size());
        return users;
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
        log.info("PUT /users request");
        User updateUser = userService.update(user);
        log.info("PUT /users response: {}", updateUser);
        return updateUser;
    }
}