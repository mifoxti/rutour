package com.example.rutour;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddPlaceFragment extends Fragment {

    private EditText placeName, placeCity, placeDescription;
    private Button loadImageButton, saveButton;
    private ImageView previewImage;
    private String imagePath = null; // Добавляем поле для пути к изображению
    private static final int IMAGE_PICK_REQUEST = 100;

    public AddPlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_place, container, false);

        placeName = view.findViewById(R.id.placeName);
        placeCity = view.findViewById(R.id.placeCity);
        placeDescription = view.findViewById(R.id.placeDescription);
        loadImageButton = view.findViewById(R.id.loadImage);
        saveButton = view.findViewById(R.id.savePlace);
        previewImage = view.findViewById(R.id.previewImage); // Добавляем ImageView для предпросмотра

        loadImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_PICK_REQUEST);
        });

        saveButton.setOnClickListener(v -> {
            // Обработчик нажатия для сохранения места
            String name = placeName.getText().toString().trim();
            String city = placeCity.getText().toString().trim();
            String description = placeDescription.getText().toString().trim();

            if (!name.isEmpty() && !city.isEmpty() && !description.isEmpty() && imagePath != null) {
                // Сохранение данных в базу данных
                DBHelper dbHelper = new DBHelper(requireContext());
                long result = dbHelper.insertPlace(name, city, description, imagePath);

                if (result != -1) {
                    Toast.makeText(requireContext(), "Место успешно сохранено", Toast.LENGTH_SHORT).show();
                    // Очистка полей после сохранения
                    placeName.setText("");
                    placeCity.setText("");
                    placeDescription.setText("");
                    previewImage.setImageResource(0); // Очистка изображения
                    imagePath = null; // Сброс пути к изображению
                } else {
                    Toast.makeText(requireContext(), "Ошибка при сохранении места", Toast.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(view, "Пожалуйста, заполните все поля и загрузите изображение", Snackbar.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Получение выбранного изображения из галереи
            Uri imageUri = data.getData();
            try {
                imagePath = saveImageToInternalStorage(imageUri);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                previewImage.setImageBitmap(bitmap); // Отображение изображения в ImageView
                Toast.makeText(requireContext(), "Изображение успешно загружено", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Метод для сохранения изображения во внутреннее хранилище и получения пути к файлу
    private String saveImageToInternalStorage(Uri imageUri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        File directory = requireContext().getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, System.currentTimeMillis() + ".jpg");

        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();

        return file.getAbsolutePath();
    }
}
