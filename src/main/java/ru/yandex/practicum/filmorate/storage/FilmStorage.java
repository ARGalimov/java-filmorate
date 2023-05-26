package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void create(Film film) throws ValidationException;

    void update(Film film) throws ValidationException, NoDataException;

    List<Film> getFilmList();

    Film getById(Integer id) throws NoDataException;

}