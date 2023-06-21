package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) throws ValidationException {
        return filmService.create(film);
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) throws ValidationException, NoDataException {
        return filmService.update(film);
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Список фильмов получен");
        return filmService.findAll();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") Integer id, @PathVariable(name = "userId") Integer userId)
            throws NoDataException {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable(name = "id") Integer id, @PathVariable(name = "userId") Integer userId)
            throws NoDataException {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/{id}")
    public Film getById(@PathVariable(name = "id") Integer id) throws NoDataException {
        return filmService.getById(id);
    }

    @GetMapping("/films/popular")
    public Set<Film> getPopular(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count);
    }

    @GetMapping("/genres")
    public List<Genre> findAllGenres() {
        List<Genre> genres = filmService.getAllGenres();
        log.info("Genres are returned: {}", genres);
        return genres;
    }

    @GetMapping("/genres/{id}")
    public Genre findGenreById(@PathVariable(name = "id") Integer id) throws NoDataException {
        Genre genre = filmService.getGenreById(id);
        log.info(String.format("Genre with id %s is returned: {}", id), genre);
        return genre;
    }

    @GetMapping("/mpa")
    public List<MPA> findAllRatings() {
        List<MPA> mpa = filmService.getAllRatings();
        log.info("Ratings are returned: {}", mpa);
        return mpa;
    }

    @GetMapping("/mpa/{id}")
    public MPA findRatingById(@PathVariable(name = "id") Integer id) throws NoDataException {
        MPA mpa = filmService.getRatingById(id);
        log.info(String.format("MPA with id %s is returned: {}", id), mpa);
        return mpa;
    }
}
