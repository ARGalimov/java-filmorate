package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@lombok.Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Integer> likes = new TreeSet<>();

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
