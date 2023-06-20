package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user) throws ValidationException;

    User update(User user) throws ValidationException, NoDataException;

    void delete(User user);

    List<User> getUserList();

    User getById(Integer id) throws NoDataException;
}