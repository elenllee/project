package com.example.project;

import com.example.project.Word;

import java.util.List;
import java.util.Random;

public class WordPicker {
    public static Word getRandomWord(List<Word> words) {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }
}
