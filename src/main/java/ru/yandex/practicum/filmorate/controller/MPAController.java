package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MPAController {
    private final FilmService filmService;

    @Autowired
    public MPAController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<MPA> findAllRatings() {
        List<MPA> mpa = filmService.getAllRatings();
        log.info("Ratings are returned: {}", mpa);
        return mpa;
    }

    @GetMapping("/{id}")
    public MPA findRatingById(@PathVariable(name = "id") Integer id) throws NoDataException {
        MPA mpa = filmService.getRatingById(id);
        log.info(String.format("MPA with id %s is returned: {}", id), mpa);
        return mpa;
    }
}
