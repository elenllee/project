package com.example.project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private static final String FILE_NAME = "words.txt";
    private List<Word> wordList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ListView wordListView = findViewById(R.id.word_list_view);
        EditText inputWord = findViewById(R.id.input_word);
        EditText inputDescription = findViewById(R.id.input_description);
        Button addWordButton = findViewById(R.id.add_word_button);

        // проверяем существование файла и создаем его при необходимости
        ensureFileExists();

        // инициализация списка слов
        wordList = WordLoader.loadWordsFromFile(getFilesDir() + "/" + FILE_NAME);
        List<String> wordDisplayList = new ArrayList<>();
        for (Word word : wordList) {
            wordDisplayList.add(word.getWord() + " - " + word.getDescription());
        }

        // настройка отображения списка слов
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wordDisplayList);
        wordListView.setAdapter(adapter);

        // добавление нового слова
        addWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = inputWord.getText().toString().trim();
                String description = inputDescription.getText().toString().trim();

                if (word.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AdminActivity.this, "все поля обязательны для заполнения", Toast.LENGTH_SHORT).show();
                    return;
                }

                // добавляем слово в список и файл
                Word newWord = new Word(word, description);
                wordList.add(newWord);
                WordSaver.addWordToFile(getFilesDir() + "/" + FILE_NAME, newWord);

                // обновление
                wordDisplayList.add(word + " - " + description);
                adapter.notifyDataSetChanged();

                // очистка поля ввода
                inputWord.setText("");
                inputDescription.setText("");

                Toast.makeText(AdminActivity.this, "слово добавлено", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // проверка существования файла и создание, если его нет
    private void ensureFileExists() {
        File file = new File(getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Toast.makeText(this, "файл создан: " + FILE_NAME, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "ошибка создания файла: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
