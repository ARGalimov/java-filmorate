package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ValidateTest {
    FilmController filmController;
    UserController userController;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
        userController = new UserController();
    }

    @Test
    void testFilmNameIsBlankOrNull() {
        Film film = new Film("", "Фильм без названия", LocalDate.of(2023, 1, 1), 90);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Название не может быть пустым!", exception.getMessage());
        Film film2 = new Film(null, "Фильм без названия", LocalDate.of(2023, 1, 1), 90);
        ValidationException exception2 = assertThrows(
                ValidationException.class,
                () -> filmController.create(film2)
        );
        assertEquals("Название не может быть пустым!", exception2.getMessage());
    }

    @Test
    void testFilmDescriptionIsTooLong() {
        String tooLongDescription = "a".repeat(202);
        Film film = new Film("Фильм с длинным описанием", tooLongDescription, LocalDate.of(2023, 1, 1), 90);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Максимальная длина описания — 200 символов!", exception.getMessage());
    }

    @Test
    void testFilmReleaseDateIsEarlier() {
        Film film = new Film("Поезд в пути к вокзалу Ла-Сьота́", "Фильм про путь до прибытия поезда на вокзал Ла-Сьота́", LocalDate.of(1895, 1, 1), 10);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года!", exception.getMessage());
    }

    @Test
    void testFilmDurationIsNotPositive() {
        Film film = new Film("Назад в будущее", "Фильм о путешествии во времени", LocalDate.of(2023, 1, 1), -90);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Продолжительность фильма должна быть положительной!", exception.getMessage());
    }

    @Test
    void testFilmIsCreatedBefore() throws ValidationException {
        Film film = new Film("Фильм", "Первый фильм", LocalDate.of(2023, 1, 1), 90);
        Film film2 = filmController.create(film);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film2)
        );
        assertEquals("Фильм уже был добавлен!", exception.getMessage());
    }

    @Test
    void testFilmIsNotCreatedBefore() {
        Film film = new Film("Фильм", "Первый фильм", LocalDate.of(2023, 1, 1), 90);
        film.setId(1);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.update(film)
        );
        assertEquals("Фильм не найден!", exception.getMessage());
    }

    @Test
    void testUserEmailIsBlankOrNullOrNotContainAt() {
        User user = new User(" ", "Login", "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @!", exception.getMessage());

        User user2 = new User(null, "Login", "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception2 = assertThrows(ValidationException.class,
                () -> userController.create(user2));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @!", exception2.getMessage());

        User user3 = new User("example.mail.ru", "Login", "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception3 = assertThrows(ValidationException.class,
                () -> userController.create(user3));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @!", exception3.getMessage());
    }

    @Test
    void testUserLoginIsBlankOrNullOrSpace() {
        User user = new User("example@mail.ru", "", "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user));
        assertEquals("Логин не может быть пустым и содержать пробелы!", exception.getMessage());

        User user2 = new User("example@mail.ru", null, "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception2 = assertThrows(ValidationException.class,
                () -> userController.create(user2));
        assertEquals("Логин не может быть пустым и содержать пробелы!", exception2.getMessage());

        User user3 = new User("example@mail.ru", "Login Space", "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception3 = assertThrows(ValidationException.class,
                () -> userController.create(user3));
        assertEquals("Логин не может быть пустым и содержать пробелы!", exception3.getMessage());
    }

    @Test
    void testUserNameIsBlankOrNull() throws ValidationException {
        User user = new User("example@mail.ru", "Login", " ", LocalDate.of(2023, 1, 1));
        userController.create(user);
        assertEquals("Login", user.getName());

        User user2 = new User("example@mail.ru", "Login", null, LocalDate.of(2023, 1, 1));
        userController.create(user2);
        assertEquals("Login", user2.getName());
    }

    @Test
    void testUserFutureBirthday() {
        User user = new User("example@mail.ru", "Login", "Name", LocalDate.of(2024, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user));
        assertEquals("Дата рождения не может быть в будущем!", exception.getMessage());
    }

    @Test
    void testUserIsCreatedBefore() throws ValidationException {
        User user = new User("example@mail.ru", "Login", "Name", LocalDate.of(2023, 1, 1));
        User user2 = userController.create(user);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user2));
        assertEquals("Пользователь уже был добавлен!", exception.getMessage());
    }

    @Test
    void validateUserUpdate() {
        User user = new User("example@mail.ru", "Login", "Name", LocalDate.of(2023, 1, 1));
        user.setId(1);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.update(user));
        assertEquals("Пользователь не найден!", exception.getMessage());
    }
}
