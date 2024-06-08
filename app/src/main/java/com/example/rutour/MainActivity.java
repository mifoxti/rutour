package com.example.rutour;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Устанавливаем слушатель для обработки нажатий на элементы Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                selectedFragment = new main_screen();
            } else if (itemId == R.id.loved) {
                selectedFragment = new LovedFragment();
            } else if (itemId == R.id.create) {
                selectedFragment = new CreateFragment();
            } else if (itemId == R.id.profile) {
                selectedFragment = new ProfileUnlogged();
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
