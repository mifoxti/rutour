package com.example.rutour;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.material.snackbar.Snackbar;

public class CreateFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlaceAdapterModern adapter;
    private Button createButton, deleteAllButton;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        createButton = view.findViewById(R.id.create);
        deleteAllButton = view.findViewById(R.id.deleteAllButton);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PlaceAdapterModern(requireContext(), true); // Передаем контекст и флаг isCreateTab
        recyclerView.setAdapter(adapter);

        createButton.setOnClickListener(v -> {
            // Переход к AddPlaceFragment для создания нового места
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddPlaceFragment())
                    .addToBackStack(null)
                    .commit();
        });

        deleteAllButton.setOnClickListener(v -> {
            // Показать диалоговое окно для подтверждения удаления всех мест
            new AlertDialog.Builder(requireContext())
                    .setTitle("Подтверждение удаления")
                    .setMessage("Вы уверены, что хотите удалить все места?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        DBHelper dbHelper = new DBHelper(requireContext());
                        dbHelper.deleteAllPlaces();
                        adapter.loadPlacesFromDatabase(); // Перезагружаем данные в адаптере
                        Snackbar.make(view, "Все места удалены", Snackbar.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });

        return view;
    }
}
