package ru.yandex.practicum;

import java.util.*;


public class WordleDictionary {

    private final List<String> words;

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
    }

    public int size() {
        return words.size();
    }

    public List<String> getWords() {
        return Collections.unmodifiableList(words);
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public String getRandomWord(Random random) {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст, нельзя выбрать слово");
        }
        int index = random.nextInt(words.size());
        return words.get(index);
    }

    public static String normalize(String word) {
        if (word == null) {
            return null;
        }
        String result = word.trim()
                .toLowerCase(Locale.forLanguageTag("ru"))
                .replace('ё', 'е');
        return result;
    }
}
