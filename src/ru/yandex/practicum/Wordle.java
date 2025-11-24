package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream("wordle.log", true),
                        StandardCharsets.UTF_8),
                true)) {

            log.println("=== Запуск Wordle ===");

            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dictionary = loader.load("words_ru.txt");

            WordleGame game = new WordleGame(dictionary, log);

            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Добро пожаловать в Wordle по-русски!");
                System.out.println("Угадайте слово из 5 русских букв. У вас 6 попыток.");
                System.out.println("Нажмите Enter на пустой строке, чтобы получить подсказку.");
                System.out.println("Введите слово \"стоп\", чтобы завершить игру досрочно.");
                System.out.println();

                boolean aborted = false;

                while (!game.isOver()) {
                    System.out.println("Осталось попыток: " + game.getRemainingSteps()
                            + ", подсказок: " + game.getRemainingHints());
                    System.out.print("Ваше слово (или \"стоп\" для выхода): ");
                    String input = scanner.nextLine().trim();

                    if ("стоп".equalsIgnoreCase(input)) {
                        System.out.println("Игра прервана. Загаданное слово: " + game.getAnswer());
                        log.println("Игра остановлена пользователем командой \"стоп\".");
                        aborted = true;
                        break;
                    }

                    if (input.isEmpty()) {
                        if (game.getRemainingHints() <= 0) {
                            System.out.println("Подсказки закончились.");
                            System.out.println();
                            continue;
                        }

                        String hint = game.suggestWord();
                        if (hint == null) {
                            System.out.println("Подходящих подсказок не найдено.");
                        } else {
                            System.out.println("Подсказка: " + hint);
                            System.out.println("Осталось подсказок: " + game.getRemainingHints());
                        }
                        System.out.println();
                        continue;
                    }

                    try {
                        String feedback = game.makeGuess(input);
                        String normalized = WordleDictionary.normalize(input);
                        System.out.println("> " + normalized);
                        System.out.println("  " + feedback);
                        System.out.println();
                    } catch (InvalidUserInputException e) {
                        System.out.println(e.getMessage());
                        log.println("Ошибка ввода: " + e.getMessage());
                    } catch (WordNotFoundInDictionaryException e) {
                        System.out.println("Такого слова нет в словаре. Попробуйте другое.");
                        log.println(e.getMessage());
                    }
                }

                if (!aborted) {
                    if (game.isWon()) {
                        System.out.println("Поздравляем! Вы отгадали слово: " + game.getAnswer());
                    } else {
                        System.out.println("Попытки закончились. Загаданное слово: " + game.getAnswer());
                    }
                }
            }

            log.println("Игра завершена.");

        } catch (DictionaryLoadException e) {
            System.err.println("Критическая ошибка: не удалось загрузить словарь.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Критическая ошибка: не удалось создать лог-файл.");
            e.printStackTrace();
        }
    }
}



