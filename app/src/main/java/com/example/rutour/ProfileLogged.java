package com.example.rutour;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class ProfileLogged extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private CheckBox creatorModeCheckBox;

    public ProfileLogged() {}

    public static ProfileLogged newInstance(String param1, String param2) {
        ProfileLogged fragment = new ProfileLogged();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_logged, container, false);

        // Найти TextView и CheckBox
        TextView helloTxt = view.findViewById(R.id.helloTxt);
        creatorModeCheckBox = view.findViewById(R.id.creatermode);

        // Получить имя пользователя из SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userLogin = sharedPreferences.getString("user_login", "User");
        helloTxt.setText("Здравствуйте, " + userLogin);

        // Установить обработчик нажатия на кнопку logoutBtn
        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> logoutUser());

        // Загрузить состояние чекбокса из SharedPreferences
        boolean isCreatorModeEnabled = sharedPreferences.getBoolean("creator_mode", false);
        creatorModeCheckBox.setChecked(isCreatorModeEnabled);

        // Установить слушатель для чекбокса
        creatorModeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Сохранить состояние чекбокса в SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("creator_mode", isChecked);
            editor.apply();
        });

        return view;
    }

    private void logoutUser() {
        // Удалить user_id из SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_id");
        editor.remove("user_login");
        editor.remove("creator_mode");
        editor.apply();

        // Переключить фрагмент на ProfileUnlogged
        Fragment profileUnloggedFragment = new ProfileUnlogged();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, profileUnloggedFragment)
                .commit();
    }
}
