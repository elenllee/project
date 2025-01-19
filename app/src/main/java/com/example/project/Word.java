package com.example.project;

public class Word {
    private String word;
    private String description;

    public Word(String word, String description) {
        this.word = word;
        this.description = description;
    }

    public String getWord() {
        return word;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
