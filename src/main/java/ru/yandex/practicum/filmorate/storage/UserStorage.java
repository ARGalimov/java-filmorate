package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    void create(User user) throws ValidationException;

    void update(User user) throws ValidationException, NoDataException;

    List<User> getUserList();

    User getById(Integer id) throws NoDataException;

}