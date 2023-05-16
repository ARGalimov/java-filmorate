package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 0;

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
        validateFilm(film);
        validateFilmCreation(films, film);
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Фильм создан");
        return films.get(film.getId());
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException {
        validateFilm(film);
        validateFilmUpdate(films, film);
        films.put(film.getId(), film);
        log.info("Фильм обновлен");
        return films.get(film.getId());
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Список фильмов получен");
        return new ArrayList<>(films.values());
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название не может быть пустым!");
            throw new ValidationException("Название не может быть пустым!");
        }
        if (film.getDescription() != null & film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов!");
            throw new ValidationException("Максимальная длина описания — 200 символов!");
        }
        if (film.getReleaseDate() != null & film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года!");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года!");
        }
        if (film.getDuration() != null & film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной!");
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
    }

    private void validateFilmCreation(Map<Integer, Film> films, Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            log.error("Фильм уже был добавлен!");
            throw new ValidationException("Фильм уже был добавлен!");
        }
    }

    private void validateFilmUpdate(Map<Integer, Film> films, Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм не найден!");
            throw new ValidationException("Фильм не найден!");
        }
    }
}
