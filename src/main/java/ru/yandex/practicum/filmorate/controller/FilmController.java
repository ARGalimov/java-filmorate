package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        validateFilm(film);
        validateFilmCreation(films, film);
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Film is created: {}", film);
        return films.get(film.getId());
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        validateFilm(film);
        validateFilmUpdate(films, film);
        films.put(film.getId(), film);
        log.info("Film is updated: {}", film);
        return films.get(film.getId());
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Films are returned: {}", films.values());
        return new ArrayList<>(films.values());
    }

    public static void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
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
