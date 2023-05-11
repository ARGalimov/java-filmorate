package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
public class UserValidator {
    public static void validateUser(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @!");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @!");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы!");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя для отображения может быть пустым — в таком случае будет использован логин!");
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    public static void validateUserCreation(Map<Integer, User> users, User user) throws ValidationException {
        if (users.containsKey(user.getId())) {
            log.error("Пользователь уже был добавлен!");
            throw new ValidationException("Пользователь уже был добавлен!");
        }
    }

    public static void validateUserUpdate(Map<Integer, User> users, User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь не найден!");
            throw new ValidationException("Пользователь не найден!");
        }
    }
}

