package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@lombok.Data
public class User {
    private Integer id;
    @Email @NotBlank @NotNull
    private String email;
    @NotBlank @NotNull
    private String login;
    private String name;
    private LocalDate birthday;
}
