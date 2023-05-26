package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class FilmValidateTest {
    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    void beforeEach() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        inMemoryUserStorage = new InMemoryUserStorage();
    }

    @Test
    void testFilmNameIsBlankOrNull() {
        Film film = new Film("", "Фильм без названия", LocalDate.of(2023, 1, 1), 90);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> inMemoryFilmStorage.create(film)
        );
        assertEquals("Название не может быть пустым!", exception.getMessage());
        Film film2 = new Film(null, "Фильм без названия", LocalDate.of(2023, 1, 1), 90);
        ValidationException exception2 = assertThrows(
                ValidationException.class,
                () -> inMemoryFilmStorage.create(film2)
        );
        assertEquals("Название не может быть пустым!", exception2.getMessage());
    }

    @Test
    void testFilmDescriptionIsTooLong() {
        String tooLongDescription = "a".repeat(202);
        Film film = new Film("Фильм с длинным описанием", tooLongDescription, LocalDate.of(2023, 1, 1), 90);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> inMemoryFilmStorage.create(film)
        );
        assertEquals("Максимальная длина описания — 200 символов!", exception.getMessage());
    }

    @Test
    void testFilmReleaseDateIsEarlier() {
        Film film = new Film("Поезд в пути к вокзалу Ла-Сьота́", "Фильм про путь до прибытия поезда на вокзал Ла-Сьота́", LocalDate.of(1895, 1, 1), 10);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> inMemoryFilmStorage.create(film)
        );
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года!", exception.getMessage());
    }

    @Test
    void testFilmDurationIsNotPositive() {
        Film film = new Film("Назад в будущее", "Фильм о путешествии во времени", LocalDate.of(2023, 1, 1), -90);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> inMemoryFilmStorage.create(film)
        );
        assertEquals("Продолжительность фильма должна быть положительной!", exception.getMessage());
    }

    @Test
    void testFilmIsCreatedBefore() throws ValidationException {
        Film film = new Film("Фильм", "Первый фильм", LocalDate.of(2023, 1, 1), 90);
        inMemoryFilmStorage.create(film);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> inMemoryFilmStorage.create(film)
        );
        assertEquals("Фильм уже был добавлен!", exception.getMessage());
    }

    @Test
    void testFilmIsNotCreatedBefore() {
        Film film = new Film("Фильм", "Первый фильм", LocalDate.of(2023, 1, 1), 90);
        film.setId(1);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> inMemoryFilmStorage.update(film)
        );
        assertEquals("Фильм не найден!", exception.getMessage());
    }
}
