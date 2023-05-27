package ru.yandex.practicum.filmorate.exception;

public class NoDataException extends RuntimeException {
    public NoDataException(String message) {
        super(message);
    }
}