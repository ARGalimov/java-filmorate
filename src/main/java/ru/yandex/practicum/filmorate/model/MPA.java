package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@lombok.Data
public class MPA {
    @NotBlank
    private Integer id;
    @Size(max = 100)
    @NotBlank
    private String name;

    public MPA(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}