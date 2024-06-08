package com.example.rutour;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class ProfileUnlogged extends Fragment {

    private EditText loginText, passwordText;
    private Button registerButton, loginButton;
    private DBHelper dbHelper;

    public ProfileUnlogged() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_unlogged, container, false);

        loginText = view.findViewById(R.id.loginText);
        passwordText = view.findViewById(R.id.passwordText);
        registerButton = view.findViewById(R.id.registerButton);
        loginButton = view.findViewById(R.id.loginButton);
        dbHelper = new DBHelper(getContext());

        registerButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> loginUser());

        return view;
    }

    private void registerUser() {
        String login = loginText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty()) {
            Snackbar.make(getView(), "Пожалуйста, заполните все поля", Snackbar.LENGTH_SHORT).show();
        } else {
            if (dbHelper.isLoginExists(login)) {
                Snackbar.make(getView(), "Логин уже существует", Snackbar.LENGTH_SHORT).show();
            } else {
                long result = dbHelper.insertUser(login, password);
                if (result != -1) {
                    saveUserIdToPreferences(getContext(), (int) result);
                    Snackbar.make(getView(), "Регистрация успешна", Snackbar.LENGTH_SHORT).show();
                    loginText.setText("");
                    passwordText.setText("");
                } else {
                    Snackbar.make(getView(), "Ошибка при регистрации", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loginUser() {
        String login = loginText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty()) {
            Snackbar.make(getView(), "Пожалуйста, заполните все поля", Snackbar.LENGTH_SHORT).show();
        } else {
            boolean exists = dbHelper.checkUser(login, password);
            if (exists) {
                int userId = dbHelper.getUserId(login);
                saveUserIdToPreferences(getContext(), userId);
                Snackbar.make(getView(), "Вход успешен", Snackbar.LENGTH_SHORT).show();
                // Переход на другой фрагмент или активити после успешного входа
            } else {
                Snackbar.make(getView(), "Неправильный логин или пароль", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserIdToPreferences(Context context, int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", userId);
        editor.apply();
    }
}
