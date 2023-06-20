package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @Override
    public User create(User user) throws ValidationException {
        validateUserCreation(users, user);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) throws ValidationException, NoDataException {
        validateUserUpdate(users, user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
            log.debug("Пользователь {} удалён", user);
        } else {
            throw new NoDataException("Пользователь не найден");
        }
    }

    @Override
    public List<User> getUserList() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Integer id) throws NoDataException {
        if (!users.containsKey(id)) {
            throw new NoDataException("Пользователь не найден");
        }
        return users.get(id);
    }

    private void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @!");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @!");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || StringUtils.containsWhitespace(user.getLogin())) {
            log.error("Логин не может быть пустым и содержать пробелы!");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя для отображения может быть пустым — в таком случае будет использован логин!");
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем!");
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
    }

    private void validateUserCreation(Map<Integer, User> users, User user) throws ValidationException {
        validateUser(user);
        if (users.containsKey(user.getId())) {
            log.error("Пользователь уже был добавлен!");
            throw new ValidationException("Пользователь уже был добавлен!");
        }
    }

    private void validateUserUpdate(Map<Integer, User> users, User user) throws NoDataException, ValidationException {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь не найден!");
            throw new NoDataException("Пользователь не найден!");
        }
    }

}