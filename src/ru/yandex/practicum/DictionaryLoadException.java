package ru.yandex.practicum;


import java.io.Serial;

public class DictionaryLoadException extends Exception {
    @Serial
    private static final long serialVersionUID = 2706866389062357604L;

    public DictionaryLoadException(String message) {
        super(message);
    }

    public DictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
