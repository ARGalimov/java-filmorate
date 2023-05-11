package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
        FilmValidator.validateFilm(film);
        FilmValidator.validateFilmCreation(films, film);
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Film is created: {}", film);
        return films.get(film.getId());
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        FilmValidator.validateFilm(film);
        FilmValidator.validateFilmUpdate(films,film);
        films.put(film.getId(),film);
        log.info("Film is updated: {}", film);
        return films.get(film.getId());
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Films are returned: {}", films.values());
        return new ArrayList<>(films.values());
    }
}
