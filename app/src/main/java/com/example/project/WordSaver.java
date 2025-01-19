package com.example.project;
import java.io.FileWriter;
import java.io.IOException;

public class WordSaver {
    public static void addWordToFile(String filePath, Word word) {
        try (FileWriter fw = new FileWriter(filePath, true)) { // true для добавления в конец файла
            fw.write(word.getWord() + "|" + word.getDescription() + "\n");
        } catch (IOException e) {
            System.err.println("ошибка записи в файл: " + e.getMessage());
        }
    }
}
