package com.example.rutour;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Устанавливаем слушатель для обработки нажатий на элементы Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            SharedPreferences sharedPreferences = this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);
            boolean isCreatorModeEnabled = sharedPreferences.getBoolean("creator_mode", false);

            if (itemId == R.id.home) {
                selectedFragment = new main_screen();
            } else if (itemId == R.id.loved) {
                if (userId != -1) {
                    selectedFragment = new LovedFragment();
                } else {
                    // Показать сообщение о том, что доступ ограничен
                    Snackbar.make(findViewById(android.R.id.content), "Доступ к избранному только у авторизированных пользователей", Snackbar.LENGTH_SHORT).show();
                }
            } else if (itemId == R.id.create) {
                if (isCreatorModeEnabled) {
                    selectedFragment = new CreateFragment();
                } else {
                    // Показать сообщение о том, что доступ ограничен
                    Snackbar.make(findViewById(android.R.id.content), "Доступ к созданию только для создателей", Snackbar.LENGTH_SHORT).show();
                }
            } else if (itemId == R.id.profile) {
                if (userId != -1) {
                    selectedFragment = new ProfileLogged();
                } else {
                    selectedFragment = new ProfileUnlogged();
                }
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // По умолчанию открываем main_screen
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new main_screen())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }
}
