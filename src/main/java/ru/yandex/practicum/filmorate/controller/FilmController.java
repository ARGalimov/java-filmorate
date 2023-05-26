package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
        filmService.create(film);
        log.info("Фильм создан");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException, NoDataException {
        filmService.update(film);
        log.info("Фильм обновлен");
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Список фильмов получен");
        return filmService.findAll();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable(name = "id") Integer id, @PathVariable(name = "userId") Integer userId)
            throws NoDataException {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable(name = "id") Integer id, @PathVariable(name = "userId") Integer userId)
            throws NoDataException {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable(name = "id") Integer id) throws NoDataException {
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count);
    }
}
