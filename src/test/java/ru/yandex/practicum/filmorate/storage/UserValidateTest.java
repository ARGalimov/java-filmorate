package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class UserValidateTest {
    InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    void beforeEach() {
        inMemoryUserStorage = new InMemoryUserStorage();
    }

    @Test
    void testUserEmailIsBlankOrNullOrNotContainAt() {
        User user = new User(" ", "Login", "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.create(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @!", exception.getMessage());

        User user2 = new User(null, "Login", "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception2 = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.create(user2));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @!", exception2.getMessage());

        User user3 = new User("example.mail.ru", "Login", "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception3 = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.create(user3));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @!", exception3.getMessage());
    }

    @Test
    void testUserLoginIsBlankOrNullOrSpace() {
        User user = new User("example@mail.ru", "", "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.create(user));
        assertEquals("Логин не может быть пустым и содержать пробелы!", exception.getMessage());

        User user2 = new User("example@mail.ru", null, "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception2 = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.create(user2));
        assertEquals("Логин не может быть пустым и содержать пробелы!", exception2.getMessage());

        User user3 = new User("example@mail.ru", "Login Space", "Name", LocalDate.of(2023, 1, 1));
        ValidationException exception3 = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.create(user3));
        assertEquals("Логин не может быть пустым и содержать пробелы!", exception3.getMessage());
    }

    @Test
    void testUserNameIsBlankOrNull() throws ValidationException {
        User user = new User("example@mail.ru", "Login", " ", LocalDate.of(2023, 1, 1));
        inMemoryUserStorage.create(user);
        assertEquals("Login", user.getName());

        User user2 = new User("example@mail.ru", "Login", null, LocalDate.of(2023, 1, 1));
        inMemoryUserStorage.create(user2);
        assertEquals("Login", user2.getName());
    }

    @Test
    void testUserFutureBirthday() {
        User user = new User("example@mail.ru", "Login", "Name", LocalDate.of(2024, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.create(user));
        assertEquals("Дата рождения не может быть в будущем!", exception.getMessage());
    }

    @Test
    void testUserIsCreatedBefore() throws ValidationException {
        User user = new User("example@mail.ru", "Login", "Name", LocalDate.of(2023, 1, 1));
        inMemoryUserStorage.create(user);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> inMemoryUserStorage.create(user));
        assertEquals("Пользователь уже был добавлен!", exception.getMessage());
    }

    @Test
    void validateUserUpdate() {
        User user = new User("example@mail.ru", "Login", "Name", LocalDate.of(2023, 1, 1));
        user.setId(1);
        NoDataException exception = assertThrows(NoDataException.class,
                () -> inMemoryUserStorage.update(user));
        assertEquals("Пользователь не найден!", exception.getMessage());
    }
}
