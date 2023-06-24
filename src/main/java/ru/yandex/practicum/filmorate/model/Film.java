package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;

@lombok.Data
@lombok.NoArgsConstructor
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Integer> likes = new HashSet<>();
    private MPA mpa;
    private List<Genre> genres = new ArrayList<>();

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration, Set<Integer> likes, List<Genre> genres, MPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
        this.genres = genres;
        this.mpa = mpa;
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
