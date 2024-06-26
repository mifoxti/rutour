package com.example.rutour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
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

    private EditText placeName, placeCity, placeDescription, placeAddress;
    private Button loadImageButton, saveButton;
    private ImageView previewImage;
    private String imagePath = null;
    private static final int IMAGE_PICK_REQUEST = 100;

    private static final String ARG_PLACE = "place";

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
        placeAddress = view.findViewById(R.id.adressPlace);
        loadImageButton = view.findViewById(R.id.loadImage);
        saveButton = view.findViewById(R.id.savePlace);
        previewImage = view.findViewById(R.id.previewImage);

        loadImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_PICK_REQUEST);
        });
        saveButton.setOnClickListener(v -> {
            String name = placeName.getText().toString().trim();
            String city = placeCity.getText().toString().trim();
            String description = placeDescription.getText().toString().trim();
            String address = placeAddress.getText().toString().trim();

            if (!name.isEmpty() && !city.isEmpty() && !description.isEmpty() && !address.isEmpty() && imagePath != null) {
                DBHelper dbHelper = new DBHelper(requireContext());
                long result;

                // Проверяем, был ли передан объект Place через аргументы
                if (getArguments() != null && getArguments().containsKey(ARG_PLACE)) {
                    // Если объект Place был передан, обновляем его данные
                    Place place = getArguments().getParcelable(ARG_PLACE);
                    if (place != null) {
                        place.setName(name);
                        place.setCity(city);
                        place.setDescription(description);
                        place.setAddress(address);
                        place.setPhotoSrc(imagePath);

                        // Вызываем метод обновления места в базе данных
                        result = dbHelper.updatePlace(place);
                    } else {
                        // Если объект Place не был передан, создаем новое место
                        result = dbHelper.insertPlace(name, city, description, imagePath, address);
                    }
                } else {
                    // Если объект Place не был передан, создаем новое место
                    result = dbHelper.insertPlace(name, city, description, imagePath, address);
                }

                if (result != -1) {
                    Toast.makeText(requireContext(), "Место успешно сохранено", Toast.LENGTH_SHORT).show();
                    placeName.setText("");
                    placeCity.setText("");
                    placeDescription.setText("");
                    placeAddress.setText("");
                    previewImage.setImageResource(0);
                    imagePath = null;
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
            Uri imageUri = data.getData();
            try {
                imagePath = saveImageToInternalStorage(imageUri);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                previewImage.setImageBitmap(bitmap);
                Toast.makeText(requireContext(), "Изображение успешно загружено", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    public static AddPlaceFragment newInstance(Place place) {
        AddPlaceFragment fragment = new AddPlaceFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PLACE, place);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Place place = getArguments().getParcelable(ARG_PLACE);
            if (place != null) {
                Log.d("AddPlaceFragment", "Name: " + place.getName());
                Log.d("AddPlaceFragment", "City: " + place.getCity());
                Log.d("AddPlaceFragment", "Description: " + place.getDescription());
                Log.d("AddPlaceFragment", "Address: " + place.getAddress());

                if (placeName != null) {
                    placeName.setText(place.getName());
                }
                if (placeCity != null) {
                    placeCity.setText(place.getCity());
                }
                if (placeDescription != null) {
                    placeDescription.setText(place.getDescription());
                }
                if (placeAddress != null) {
                    placeAddress.setText(place.getAddress());
                }
                imagePath = place.getPhotoSrc();
            }
        }
    }
}