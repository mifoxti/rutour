package com.example.rutour;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private List<Place> places;
    private DBHelper dbHelper;
    private Context context;

    public void setPlaces(List<Place> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    public PlaceAdapter(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
        this.places = new ArrayList<>(); // Инициализируем список
        loadPlacesFromDatabase();
    }

    public void loadPlacesFromDatabase() {
        // Пример кода для загрузки данных из базы данных
        this.places = dbHelper.getAllPlaces(); // Предполагается, что у вас есть метод в DBHelper для получения всех мест
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView placeImageView; // Добавьте это поле
        public TextView nameTextView;
        public TextView cityTextView;
        public MaterialButton likeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            placeImageView = itemView.findViewById(R.id.placeImage); // Инициализируйте элемент
            nameTextView = itemView.findViewById(R.id.placeName);
            cityTextView = itemView.findViewById(R.id.placeCity);
            likeButton = itemView.findViewById(R.id.galleryBtnLove);
        }
    }

    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceAdapter.ViewHolder holder, int position) {
        Place place = places.get(position);

        holder.nameTextView.setText(place.getName());
        holder.cityTextView.setText(place.getCity());

        // Загрузите изображение с помощью Glide
        Glide.with(context)
                .load(place.getPhotoSrc()) // Убедитесь, что getPhotoSrc() возвращает правильный URL или путь к изображению
                .placeholder(R.drawable.zaradye) // Изображение-заполнитель, пока грузится основное изображение
                .error(R.drawable.zaradye) // Изображение для случая ошибки загрузки
                .into(holder.placeImageView);

        // Получаем user ID из SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        // Проверяем, добавлено ли место в избранное
        final boolean[] isLoved = {dbHelper.isPlaceLovedByUser(userId, place.getId())};
        if (isLoved[0]) {
            holder.likeButton.setIconResource(R.drawable.ic_heart_filled);
        } else {
            holder.likeButton.setIconResource(R.drawable.ic_heart);
        }

        // Обработчик нажатия на кнопку "Посмотреть"
        holder.itemView.findViewById(R.id.viewButton).setOnClickListener(v -> {
            // Создаем новый экземпляр PlaceDetailsFragment
            PlaceDetailsFragment fragment = PlaceDetailsFragment.newInstance(place.getId());
            // Получаем менеджер фрагментов и начинаем транзакцию
            androidx.fragment.app.FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
            androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            // Заменяем текущий фрагмент на PlaceDetailsFragment
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null); // Добавляем транзакцию в стек возврата
            fragmentTransaction.commit(); // Применяем транзакцию
        });

        // Обработчик нажатия на кнопку "Лайк"
        holder.likeButton.setOnClickListener(v -> {

            if (userId != -1) {
                // Если userId не равен -1, обрабатываем нажатие на кнопку
                if (isLoved[0]) {
                    dbHelper.deleteUserPlace(userId, place.getId());
                    holder.likeButton.setIconResource(R.drawable.ic_heart);
                } else {
                    dbHelper.insertUserPlace(userId, place.getId());
                    holder.likeButton.setIconResource(R.drawable.ic_heart_filled);
                }
                // Обновляем флаг избранности для текущего места
                isLoved[0] = !isLoved[0];
            } else {
                // Если userId равен -1, показываем Snackbar
                Snackbar.make(holder.itemView, "Войдите в аккаунт, чтобы лайкнуть", Snackbar.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return places.size();
    }
}
