package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody User user) throws ValidationException {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException, NoDataException {
        return userService.update(user);
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Список пользователей получен");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable(name = "id") Integer id) throws NoDataException {
        return userService.getById(id);
    }


    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(name = "id") Integer id, @PathVariable(name = "friendId") Integer friendId)
            throws NoDataException {
        userService.addFriend(id, friendId);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable(name = "id") Integer id, @PathVariable(name = "friendId") Integer friendId)
            throws NoDataException {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable(name = "id") Integer id) throws NoDataException {
        return userService.getFriends(userService.getById(id).getFriends());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "otherId") Integer otherId) throws NoDataException {
        return userService.getCommonFriends(id, otherId);
    }
}

