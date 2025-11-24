package ru.yandex.practicum;


import java.io.Serial;

public class WordNotFoundInDictionaryException extends Exception {
    @Serial
    private static final long serialVersionUID = -7458190255075496244L;

    public WordNotFoundInDictionaryException(String word) {
        super("Слово \"" + word + "\" отсутствует в словаре");
    }
}
