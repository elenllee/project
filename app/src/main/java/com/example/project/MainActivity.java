package com.example.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // кнопки выбора роли
        Button adminButton = findViewById(R.id.btn_admin);
        Button playerButton = findViewById(R.id.btn_player);
        Button rulesButton = findViewById(R.id.btn_rules);

        // администратор
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // переход на экран администратора
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        // игрок
        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // переход на экран игрока
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });

        // правила
        rulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // показ диалогового окна с правилами
                showRulesDialog();
            }
        });
    }
    private void showRulesDialog() {
        // правила
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("правила игры")
                .setMessage("1. угадывайте слово по буквам.\n"
                        + "2. крутите барабан, чтобы получать очки или бонусы.\n"
                        + "3. у вас ограниченное количество попыток.\n"
                        + "4. если вы угадали слово целиком, вы выигрываете.\n"
                        + "5. если ваши попытки закончились, игра завершена.")
                .setPositiveButton("понятно", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}
