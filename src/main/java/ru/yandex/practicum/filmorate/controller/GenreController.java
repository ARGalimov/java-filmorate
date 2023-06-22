package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> findAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        log.info("Genres are returned: {}", genres);
        return genres;
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable(name = "id") Integer id) throws NoDataException {
        Genre genre = genreService.getGenreById(id);
        log.info(String.format("Genre with id %s is returned: {}", id), genre);
        return genre;
    }
}
