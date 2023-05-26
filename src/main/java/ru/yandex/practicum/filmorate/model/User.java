package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@lombok.Data
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends = new TreeSet<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
