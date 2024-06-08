package com.example.rutour;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class LovedFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private DBHelper dbHelper;

    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;

    public LovedFragment() {}

    public static LovedFragment newInstance(String param1, String param2) {
        LovedFragment fragment = new LovedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(getContext()); // Инициализация dbHelper
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loved, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.lovedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter
        placeAdapter = new PlaceAdapter(getContext());
        recyclerView.setAdapter(placeAdapter);

        // Load loved places from database
        loadLovedPlaces();

        return view;
    }

    private void loadLovedPlaces() {
        // Проверяем, что dbHelper был инициализирован
        if (dbHelper == null) {
            dbHelper = new DBHelper(getContext());
        }

        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        // Get the list of place IDs liked by the user
        List<Integer> lovedPlaceIds = dbHelper.getUserLovedPlaceIds(userId);

        // Get the details of loved places from the database
        List<Place> lovedPlaces = dbHelper.getPlacesByIds(lovedPlaceIds);

        // Update the adapter with the loved places
        placeAdapter.setPlaces(lovedPlaces);
    }
}
