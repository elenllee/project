package com.example.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity {
    private static final String FILE_NAME = "words.txt";
    private Word currentWord;
    private char[] guessedWordState;
    private List<Character> guessedLetters;
    private int totalScore = 0;
    private int remainingAttempts = 3;
    private boolean isGameOver = false;
    private boolean hasSpunWheel = false;

    private Random random = new Random();
    private String[] wheelOptions = {
            "+100 очков", "+200 очков", "открыть букву",
            "доп. попытка", "конец игры", "+50 очков", "пустое поле"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        TextView wordDescription = findViewById(R.id.word_description);
        TextView currentWordStateView = findViewById(R.id.current_word_state);
        EditText guessLetterInput = findViewById(R.id.guess_letter_input);
        Button checkLetterButton = findViewById(R.id.check_letter_button);
        Button spinWheelButton = findViewById(R.id.spin_wheel_button);
        Button guessWordButton = findViewById(R.id.guess_word_button);
        guessWordButton.setEnabled(false);
        ImageView wheelImage = findViewById(R.id.wheel_image);
        TextView gameResult = findViewById(R.id.game_result);
        TextView totalScoreView = findViewById(R.id.total_score);
        TextView remainingAttemptsView = findViewById(R.id.remaining_attempts);

        // загрузка случайного слова из файла
        currentWord = getRandomWord();
        if (currentWord == null) {
            Toast.makeText(this, "нет слов для игры", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // инициализация состояния слова
        guessedWordState = new char[currentWord.getWord().length()];
        guessedLetters = new ArrayList<>();
        for (int i = 0; i < guessedWordState.length; i++) {
            guessedWordState[i] = '_';
        }

        // установка описания слова
        wordDescription.setText(currentWord.getDescription());
        updateWordStateView(currentWordStateView);
        updateScoreAndAttemptsView(totalScoreView, remainingAttemptsView);

        // блокировка ввода до вращения барабана
        checkLetterButton.setEnabled(false);
        guessLetterInput.setEnabled(false);

        // обработка нажатия кнопки проверки буквы
        checkLetterButton.setOnClickListener(v -> {
            if (isGameOver) return;

            String input = guessLetterInput.getText().toString().trim().toLowerCase();
            if (input.isEmpty() || input.length() != 1) {
                Toast.makeText(PlayerActivity.this, "введите одну букву", Toast.LENGTH_SHORT).show();
                return;
            }

            char guessedLetter = input.charAt(0);
            if (guessedLetters.contains(guessedLetter)) {
                Toast.makeText(PlayerActivity.this, "вы уже угадали эту букву", Toast.LENGTH_SHORT).show();
                return;
            }

            guessedLetters.add(guessedLetter);
            boolean isCorrect = false;

            // Проверяем букву
            for (int i = 0; i < currentWord.getWord().length(); i++) {
                if (currentWord.getWord().charAt(i) == guessedLetter) {
                    guessedWordState[i] = guessedLetter;
                    isCorrect = true;
                }
            }

            if (isCorrect) {
                Toast.makeText(PlayerActivity.this, "правильно!", Toast.LENGTH_SHORT).show();
                totalScore += 100; // очки за правильную букву
            } else {
                Toast.makeText(PlayerActivity.this, "неправильно!", Toast.LENGTH_SHORT).show();
                remainingAttempts--;

                if (remainingAttempts <= 0) {
                    gameResult.setText("конец игры! у вас не осталось попыток.");
                    endGame();
                    return;
                }
            }

            // обновление состояния слова и баллы
            updateWordStateView(currentWordStateView);
            updateScoreAndAttemptsView(totalScoreView, remainingAttemptsView);

            // проверка завершение игры
            if (new String(guessedWordState).equals(currentWord.getWord())) {
                gameResult.setText("поздравляем! вы угадали слово: " + currentWord.getWord());
                endGame();
            }

            guessLetterInput.setText("");

            // снова блоковка ввода до следующего вращения барабана
            hasSpunWheel = false;
            checkLetterButton.setEnabled(false);
            guessLetterInput.setEnabled(false);
            guessLetterInput.setEnabled(false);
            guessWordButton.setEnabled(false);
        });

        guessWordButton.setOnClickListener(v -> {
            if (isGameOver) return;

            // диалог для ввода слова
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PlayerActivity.this);
            builder.setTitle("назовите слово");

            // создание поля ввода
            final EditText input = new EditText(PlayerActivity.this);
            input.setHint("введите полное слово");
            builder.setView(input);

            // кнопка подтверждения
            builder.setPositiveButton("ок", (dialog, which) -> {
                String guessedWord = input.getText().toString().trim().toLowerCase();

                if (guessedWord.equals(currentWord.getWord())) {
                    gameResult.setText("поздравляем! вы угадали слово: " + currentWord.getWord());
                    totalScore += 500; // Бонусные очки за слово
                    updateScoreAndAttemptsView(totalScoreView, remainingAttemptsView);
                    endGame();
                } else {
                    Toast.makeText(PlayerActivity.this, "неправильное слово!", Toast.LENGTH_SHORT).show();
                    remainingAttempts--;

                    if (remainingAttempts <= 0) {
                        gameResult.setText("конец игры! у вас не осталось попыток.");
                        endGame();
                    } else {
                        // после ошибки блокировка угадывание букв и слова
                        checkLetterButton.setEnabled(false);
                        guessLetterInput.setEnabled(false);
                        guessWordButton.setEnabled(false);

                        hasSpunWheel = false; // пользователь должен снова крутить барабан
                    }
                    updateScoreAndAttemptsView(totalScoreView, remainingAttemptsView);
                }
            });

            // кнопка отмены
            builder.setNegativeButton("отмена", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        // обработка нажатия кнопки кручения колеса
        spinWheelButton.setOnClickListener(v -> {
            if (isGameOver || hasSpunWheel) return;

            int resultIndex = random.nextInt(wheelOptions.length);
            String result = wheelOptions[resultIndex];

            // вращение барабана с задержкой
            spinWheelAnimation(wheelImage, resultIndex, () -> {
                Toast.makeText(PlayerActivity.this, "результат барабана: " + result, Toast.LENGTH_SHORT).show();
                handleWheelResult(result, currentWordStateView, totalScoreView, remainingAttemptsView, gameResult);
                hasSpunWheel = true;

                // разблокировка ввод после вращения
                checkLetterButton.setEnabled(true);
                guessLetterInput.setEnabled(true);
                guessWordButton.setEnabled(true);
            });
        });
    }

    private void spinWheelAnimation(ImageView wheelImage, int resultIndex, Runnable onComplete) {
        int sectors = wheelOptions.length;
        int anglePerSector = 360 / sectors;
        int randomOffset = random.nextInt(anglePerSector);
        int targetAngle = 360 * 5 + resultIndex * anglePerSector + randomOffset;

        wheelImage.animate()
                .rotation(targetAngle)
                .setDuration(2000)
                .withEndAction(onComplete)
                .start();
    }

    private void handleWheelResult(String result, TextView currentWordStateView, TextView totalScoreView,
                                   TextView remainingAttemptsView, TextView gameResult) {
        switch (result) {
            case "+100 очков":
                totalScore += 100;
                break;
            case "+200 очков":
                totalScore += 200;
                break;
            case "открыть букву":
                revealRandomLetter();
                break;
            case "доп. попытка":
                remainingAttempts++;
                break;
            case "конец игры":
                gameResult.setText("конец игры! барабан завершил вашу игру.");
                endGame();
                return;
            case "+50 очков":
                totalScore += 50;
                break;
            case "пустое поле":
                break;
        }
        updateWordStateView(currentWordStateView);
        updateScoreAndAttemptsView(totalScoreView, remainingAttemptsView);
    }

    private void revealRandomLetter() {
        for (int i = 0; i < guessedWordState.length; i++) {
            if (guessedWordState[i] == '_') {
                guessedWordState[i] = currentWord.getWord().charAt(i);
                break;
            }
        }
    }

    private void endGame() {
        isGameOver = true;
    }

    private Word getRandomWord() {
        List<Word> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(FILE_NAME)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String word = parts[0].trim().toLowerCase();
                    String description = parts[1].trim();
                    words.add(new Word(word, description));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (words.isEmpty()) return null;

        return words.get(random.nextInt(words.size()));
    }

    private void updateScoreAndAttemptsView(TextView totalScoreView, TextView remainingAttemptsView) {
        totalScoreView.setText("счет: " + totalScore);
        remainingAttemptsView.setText("оставшиеся попытки: " + remainingAttempts);
    }

    private void updateWordStateView(TextView currentWordStateView) {
        StringBuilder state = new StringBuilder();
        for (char c : guessedWordState) {
            state.append(c).append(" ");
        }
        currentWordStateView.setText(state.toString().trim());
    }
}
