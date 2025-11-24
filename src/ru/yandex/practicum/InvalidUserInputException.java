package ru.yandex.practicum;


import java.io.Serial;

public class InvalidUserInputException extends Exception {
    @Serial
    private static final long serialVersionUID = 2645952180452518809L;

    public InvalidUserInputException(String message) {
        super(message);
    }
}
