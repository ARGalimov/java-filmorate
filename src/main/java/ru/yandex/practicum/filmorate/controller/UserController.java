package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {
        UserValidator.validateUser(user);
        UserValidator.validateUserCreation(users,user);
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Пользователь создан");
        return users.get(user.getId());
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws ValidationException {
        UserValidator.validateUser(user);
        UserValidator.validateUserUpdate(users, user);
        users.put(user.getId(),user);
        log.info("Пользователь обновлен");
        return users.get(user.getId());
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Список пользователей получен");
        return new ArrayList<>(users.values());
    }
}
