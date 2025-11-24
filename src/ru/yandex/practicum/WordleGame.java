package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {

    private static final int MAX_STEPS = 6;
    private static final int MAX_HINTS = 3;

    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final Random random = new Random();

    private String answer;
    private int remainingSteps;

    private final List<String> guesses = new ArrayList<>();
    private final List<String> hintsGiven = new ArrayList<>();

    private final Set<Character> requiredLetters = new LinkedHashSet<>();
    private final Set<Character> forbiddenLetters = new LinkedHashSet<>();
    private final char[] knownPositions = new char[5];

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        startNewGame(dictionary.getRandomWord(random));
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter log, String fixedAnswer) {
        this.dictionary = dictionary;
        this.log = log;
        startNewGame(fixedAnswer);
    }

    private void startNewGame(String answer) {
        this.answer = WordleDictionary.normalize(answer);
        this.remainingSteps = MAX_STEPS;

        guesses.clear();
        hintsGiven.clear();
        requiredLetters.clear();
        forbiddenLetters.clear();
        Arrays.fill(knownPositions, '\0');

        log.println("Новая игра. Загаданное слово: " + this.answer);
    }

    public int getRemainingSteps() {
        return remainingSteps;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isWon() {
        return !guesses.isEmpty() && guesses.get(guesses.size() - 1).equals(answer);
    }

    public boolean isOver() {
        return isWon() || remainingSteps <= 0;
    }

    public int getRemainingHints() {
        return MAX_HINTS - hintsGiven.size();
    }

    public String makeGuess(String rawInput)
            throws InvalidUserInputException, WordNotFoundInDictionaryException {

        String guess = WordleDictionary.normalize(rawInput);

        if (guess == null || guess.isEmpty()) {
            throw new InvalidUserInputException("Введите слово из пяти русских букв.");
        }
        if (guess.length() != 5) {
            throw new InvalidUserInputException("Слово должно состоять из пяти букв.");
        }
        if (!guess.matches("[а-я]+")) {
            throw new InvalidUserInputException("Разрешены только русские буквы.");
        }
        if (!dictionary.contains(guess)) {
            throw new WordNotFoundInDictionaryException(guess);
        }
        if (remainingSteps <= 0) {
            throw new InvalidUserInputException("Попытки уже закончились.");
        }

        remainingSteps--;
        guesses.add(guess);

        String feedback = compareWithAnswer(guess);
        updateConstraints(guess, feedback);

        log.println("Ход: " + guess + " -> " + feedback
                + ", осталось попыток: " + remainingSteps);
        return feedback;
    }

    private String compareWithAnswer(String guess) {
        char[] result = new char[5];

        Map<Character, Integer> counts = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            char c = answer.charAt(i);
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }

        for (int i = 0; i < 5; i++) {
            char g = guess.charAt(i);
            if (g == answer.charAt(i)) {
                result[i] = '+';
                counts.put(g, counts.get(g) - 1);
            }
        }

        for (int i = 0; i < 5; i++) {
            if (result[i] != 0) {
                continue;
            }
            char g = guess.charAt(i);
            int cnt = counts.getOrDefault(g, 0);
            if (cnt > 0) {
                result[i] = '^';
                counts.put(g, cnt - 1);
            } else {
                result[i] = '-';
            }
        }

        return new String(result);
    }

    private void updateConstraints(String guess, String mark) {
        for (int i = 0; i < 5; i++) {
            char g = guess.charAt(i);
            char m = mark.charAt(i);

            switch (m) {
                case '+':
                    knownPositions[i] = g;
                    requiredLetters.add(g);
                    break;
                case '^':
                    requiredLetters.add(g);
                    break;
                case '-':
                    if (!requiredLetters.contains(g)) {
                        forbiddenLetters.add(g);
                    }
                    break;
                default:
                    throw new RuntimeException("Неожиданный символ подсказки: " + m);
            }
        }
    }

    public String suggestWord() {
        if (getRemainingHints() <= 0) {
            log.println("Запрошена подсказка, но лимит подсказок исчерпан.");
            return null;
        }

        List<String> candidates = new ArrayList<>();

        outer:
        for (String word : dictionary.getWords()) {
            if (guesses.contains(word) || hintsGiven.contains(word)) {
                continue;
            }

            for (char c : word.toCharArray()) {
                if (forbiddenLetters.contains(c)) {
                    continue outer;
                }
            }

            for (char need : requiredLetters) {
                if (word.indexOf(need) < 0) {
                    continue outer;
                }
            }

            for (int i = 0; i < 5; i++) {
                if (knownPositions[i] != '\0' && word.charAt(i) != knownPositions[i]) {
                    continue outer;
                }
            }

            candidates.add(word);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        String chosen = candidates.get(random.nextInt(candidates.size()));
        hintsGiven.add(chosen);
        log.println("Подсказка: " + chosen + " (осталось подсказок: " + getRemainingHints() + ")");
        return chosen;
    }
}
