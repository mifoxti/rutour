package com.example.rutour;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

public class PlaceDetailsFragment extends Fragment {

    private int placeId;
    private TextView placeName, placeCity, placeDescription, placeAddress;
    private ImageView placeImage;
    private View view;
    public PlaceDetailsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_details, container, false);

        placeName = view.findViewById(R.id.placeName);
        placeCity = view.findViewById(R.id.placeCity);
        placeDescription = view.findViewById(R.id.placeDescription);
        placeAddress = view.findViewById(R.id.placeAddress);
        placeImage = view.findViewById(R.id.placeImage);

        if (getArguments() != null) {
            placeId = getArguments().getInt("place_id");
            loadPlaceDetails();
        }

        return view;
    }

    private void loadPlaceDetails() {
        DBHelper dbHelper = new DBHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DBHelper.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(placeId)};

        Cursor cursor = db.query(DBHelper.TABLE_PLACES, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME));
            String city = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CITY));
            String photoSrc = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PHOTO_SRC));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ADDRESS));

            placeName.setText(name);
            placeCity.setText(city);
            placeDescription.setText(description);
            placeAddress.setText(address);
            Log.d("PLACEADRES", "loadPlaceDetails: " + address);


            Glide.with(requireContext())
                    .load(photoSrc)
                    .centerCrop()
                    .into(placeImage);

            cursor.close();
        }

        db.close();
    }
}
