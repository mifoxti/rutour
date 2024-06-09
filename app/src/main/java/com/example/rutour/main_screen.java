package com.example.rutour;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

public class main_screen extends Fragment {

    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;
    private SearchView searchView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public main_screen() {
        // Required empty public constructor
    }

    public static main_screen newInstance(String param1, String param2) {
        main_screen fragment = new main_screen();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter
        placeAdapter = new PlaceAdapter(getContext());
        recyclerView.setAdapter(placeAdapter);

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchView);
        setupSearchView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // При возобновлении фрагмента, загружаем данные заново
        placeAdapter.loadPlacesFromDatabase();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Нет необходимости обрабатывать отправку текста, так как мы обрабатываем изменение текста в режиме реального времени
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Обновляем список мест в соответствии с текстом поиска
                placeAdapter.filter(newText);
                return true;
            }
        });
    }
}
