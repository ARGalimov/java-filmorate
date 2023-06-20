package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@lombok.Data
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Map<Integer, Boolean> friends = new HashMap<>();

    public User() { }

    public User(Integer id, String email, String login, String name, LocalDate birthday, Map<Integer, Boolean> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
    }
    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
