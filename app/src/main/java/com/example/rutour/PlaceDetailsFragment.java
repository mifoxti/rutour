package com.example.rutour;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class PlaceDetailsFragment extends Fragment {

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeCity;
    private TextView placeDescription;
    private TextView placeAddress;
    private MaterialButton loveButton;
    private DBHelper dbHelper;
    private boolean isLoved;
    private int placeId;
    private int userId;

    private static final String ARG_PLACE_ID = "place_id";

    public static PlaceDetailsFragment newInstance(int placeId) {
        PlaceDetailsFragment fragment = new PlaceDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PLACE_ID, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeId = getArguments().getInt(ARG_PLACE_ID);
        }
        dbHelper = new DBHelper(getContext());
        userId = getUserIdFromPreferences(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_details, container, false);

        placeImage = view.findViewById(R.id.placeImage);
        placeName = view.findViewById(R.id.placeName);
        placeCity = view.findViewById(R.id.placeCity);
        placeDescription = view.findViewById(R.id.placeDescription);
        placeAddress = view.findViewById(R.id.placeAddress);
        loveButton = view.findViewById(R.id.favoriteButton);

        loadPlaceDetails();

        loveButton.setOnClickListener(v -> {
            if (userId != -1) {
                isLoved = !isLoved;
                updateLoveButtonIcon();
                handleLoveButtonClick();
            } else {
                Snackbar.make(view, "Пользователь не авторизован", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void loadPlaceDetails() {
        Place place = dbHelper.getPlaceById(placeId);
        if (place != null) {
            Glide.with(this).load(place.getPhotoSrc()).into(placeImage);
            placeName.setText(place.getName());
            placeCity.setText(place.getCity());
            placeDescription.setText(place.getDescription());
            placeAddress.setText(place.getAddress());
        }
        isLoved = dbHelper.isPlaceLovedByUser(userId, placeId);
        updateLoveButtonIcon();
    }

    private void updateLoveButtonIcon() {
        if (isLoved) {
            loveButton.setIconResource(R.drawable.ic_heart_filled);
        } else {
            loveButton.setIconResource(R.drawable.ic_heart);
        }
    }

    private void handleLoveButtonClick() {
        if (isLoved) {
            dbHelper.insertUserPlace(userId, placeId);
            Snackbar.make(getView(), "Место добавлено в избранное", Snackbar.LENGTH_LONG).show();
        } else {
            dbHelper.deleteUserPlace(userId, placeId);
            Snackbar.make(getView(), "Место удалено из избранного", Snackbar.LENGTH_LONG).show();
        }
    }

    private int getUserIdFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1); // -1 если user_id не найден
    }
}
