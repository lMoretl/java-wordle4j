package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String fileName) throws DictionaryLoadException {
        List<String> words = new ArrayList<>();
        Path path = Path.of(fileName);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = WordleDictionary.normalize(line);

                if (normalized.length() == 5 && normalized.matches("[а-я]+")) {
                    words.add(normalized);
                }
            }
        } catch (IOException e) {
            log.println("Ошибка при загрузке словаря: " + e.getMessage());
            throw new DictionaryLoadException("Не удалось загрузить словарь из файла " + fileName, e);
        }

        if (words.isEmpty()) {
            throw new DictionaryLoadException("Словарь пуст или не содержит подходящих слов.");
        }

        log.println("Словарь загружен, подходящих слов: " + words.size());
        return new WordleDictionary(words);
    }
}
