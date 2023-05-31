package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film create(Film film) throws ValidationException {
        filmStorage.create(film);
        return film;
    }

    public Film update(Film film) throws ValidationException, NoDataException {
        filmStorage.update(film);
        return film;
    }

    public List<Film> findAll() {
        return filmStorage.getFilmList();
    }

    public Film getById(Integer id) throws NoDataException {
        return filmStorage.getById(id);
    }

    public Film addLike(Integer id, Integer userId) throws NoDataException {
        Film film = filmStorage.getById(id);
        Set<Integer> likes = film.getLikes();
        likes.add(userId);
        return film;
    }

    public Film deleteLike(Integer id, Integer userId) throws NoDataException {
        Film film = filmStorage.getById(id);
        Set<Integer> likes = film.getLikes();
        if (!likes.contains(userId)) {
            throw new NoDataException("Пользователь не найден");
        }
        likes.remove(userId);
        return film;
    }

    public List<Film> getPopular(Integer count) {
        List<Film> popular = filmStorage.getFilmList();
        if (popular.size() <= 1) {
            return popular;
        } else {
            return popular.stream()
                    .sorted((film1, film2) -> {
                        int result = Integer.compare(film1.getLikes().size(), film2.getLikes().size());
                        result = -1 * result;
                        return result;
                    })
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }
}
