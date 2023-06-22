package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final Integer MAX_QUANTITY_POPULAR_FILMS = 10;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film create(Film film) throws ValidationException {
        return filmStorage.create(film);
    }

    public Film update(Film film) throws ValidationException, NoDataException {
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.getFilmList();
    }

    public Film getById(Integer id) throws NoDataException {
        return filmStorage.getById(id);
    }

    public void addLike(Integer id, Integer userId) throws NoDataException {
        Film film = filmStorage.getById(id);
        Set<Integer> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        filmStorage.update(film);
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

    public Set<Film> getPopular(Integer count) {
        Comparator<Film> filmLikeComparator = (film1, film2) -> {
            if (film1.getLikes().size() == film2.getLikes().size()) {
                return (int) (film1.getId() - film2.getId());
            } else {
                return film1.getLikes().size() - film2.getLikes().size();
            }
        };
        Set<Film> popularFilms = new TreeSet<>(filmLikeComparator.reversed());
        List<Film> films = filmStorage.getFilmList();
        popularFilms.addAll(films);
        if (Objects.isNull(count)) {
            count = MAX_QUANTITY_POPULAR_FILMS;
        }
        return popularFilms.stream().limit(count).collect(Collectors.toSet());
    }
}
