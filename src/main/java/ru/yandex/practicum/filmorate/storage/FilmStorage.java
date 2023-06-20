package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface FilmStorage {

    Film create(Film film) throws ValidationException;

    Film update(Film film) throws ValidationException, NoDataException;

    void delete(Film film);

    List<Film> getFilmList();

    Film getById(Integer id) throws NoDataException;

    List<Genre> getAllGenres();

    Genre getGenreById(Integer id);

    List<MPA> getAllRatings();

    MPA getRatingById(Integer id);
}