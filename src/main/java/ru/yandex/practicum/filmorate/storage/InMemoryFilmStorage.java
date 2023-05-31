package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 0;

    @Override
    public void create(Film film) throws ValidationException {
        validateFilmCreation(films, film);
        film.setId(++id);
        films.put(film.getId(), film);
    }

    @Override
    public void update(Film film) throws ValidationException, NoDataException {
        validateFilmUpdate(films, film);
        films.put(film.getId(), film);
    }

    @Override
    public List<Film> getFilmList() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(Integer id) throws NoDataException {
        if (!films.containsKey(id)) {
            throw new NoDataException("Фильм не существует");
        }
        return films.get(id);
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название не может быть пустым!");
            throw new ValidationException("Название не может быть пустым!");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов!");
            throw new ValidationException("Максимальная длина описания — 200 символов!");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года!");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года!");
        }

        if (film.getDuration() != null && film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной!");
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
    }

    private void validateFilmCreation(Map<Integer, Film> films, Film film) throws ValidationException {
        validateFilm(film);
        if (films.containsKey(film.getId())) {
            log.error("Фильм уже был добавлен!");
            throw new ValidationException("Фильм уже был добавлен!");
        }
    }

    private void validateFilmUpdate(Map<Integer, Film> films, Film film) throws NoDataException, ValidationException {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.error("Фильм не найден!");
            throw new NoDataException("Фильм не найден!");
        }
    }
}
