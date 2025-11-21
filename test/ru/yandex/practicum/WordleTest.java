package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    @Test
    void normalize_replacesYoAndLowercases() {
        String norm = WordleDictionary.normalize(" ЁЖиК");
        assertEquals("ежик", norm);
    }

    @Test
    void gameCompare_simpleExample() throws Exception {
        List<String> words = Arrays.asList("герой", "гонец");
        WordleDictionary dict = new WordleDictionary(words);
        PrintWriter log = new PrintWriter(System.out, true);

        WordleGame game = new WordleGame(dict, log, "герой");

        String feedback = game.makeGuess("гонец");
        assertEquals("+^-^-", feedback);
    }

    @Test
    void suggestWord_returnsWordFromDictionary() {
        List<String> words = Arrays.asList("герой", "гонец");
        WordleDictionary dict = new WordleDictionary(words);
        PrintWriter log = new PrintWriter(System.out, true);

        WordleGame game = new WordleGame(dict, log, "герой");
        String hint = game.suggestWord();

        assertTrue(words.contains(hint));
    }
}
