package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class FilmValidator {
    public static void validateFilm(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.error("Название не может быть пустым!");
            throw new ValidationException("Название не может быть пустым!");
        }
        if (film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов!");
            throw new ValidationException("Максимальная длина описания — 200 символов!");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года!");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года!");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной!");
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
    }

    public static void validateFilmCreation(Map<Integer, Film> films, Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            log.error("Фильм уже был добавлен!");
            throw new ValidationException("Фильм уже был добавлен!");
        }
    }

    public static void validateFilmUpdate(Map<Integer, Film> films, Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм не найден!");
            throw new ValidationException("Фильм не найден!");
        }
    }
}
