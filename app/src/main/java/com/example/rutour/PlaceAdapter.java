package com.example.rutour;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private Context context;
    private List<Place> placeList;

    public PlaceAdapter(Context context) {
        this.context = context;
        this.placeList = new ArrayList<>();
        loadPlacesFromDatabase();
    }

    public void loadPlacesFromDatabase() { // Изменён на public
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        placeList.clear(); // Очистка списка перед загрузкой

        Cursor cursor = db.query(DBHelper.TABLE_PLACES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CITY));
                String photoSrc = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PHOTO_SRC));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ADDRESS));

                Place place = new Place(id, name, city, photoSrc, description, address);
                placeList.add(place);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        notifyDataSetChanged(); // Уведомляем адаптер об изменении данных
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = placeList.get(position);
        holder.titleTextView.setText(place.getName());
        holder.cityTextView.setText(place.getCity());
        // Загрузка изображения с помощью Glide
        Glide.with(context)
                .load(place.getPhotoSrc())
                .centerCrop()
                .into(holder.photoImageView);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        ImageView photoImageView;
        TextView titleTextView;
        TextView cityTextView;
        MaterialButton viewButton;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.placeImage);
            titleTextView = itemView.findViewById(R.id.placeName);
            cityTextView = itemView.findViewById(R.id.placeCity);
            viewButton = itemView.findViewById(R.id.viewButton);

            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Обработка нажатия кнопки
                }
            });
        }
    }
}
