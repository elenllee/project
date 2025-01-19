package com.example.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordLoader {
    public static List<Word> loadWordsFromFile(String filePath) {
        List<Word> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String word = parts[0].trim();
                    String description = parts[1].trim();
                    words.add(new Word(word, description));
                }
            }
        } catch (IOException e) {
            System.err.println("ошибка чтения файла: " + e.getMessage());
        }
        return words;
    }
}


