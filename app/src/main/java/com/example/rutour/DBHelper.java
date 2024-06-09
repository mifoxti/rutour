package com.example.rutour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "places.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_PLACES = "Place";
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_USERS_PLACES = "UsersPlaces";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_PHOTO_SRC = "photo_src";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_PLACE_ID = "place_id";

    private static final String TABLE_CREATE_PLACES =
            "CREATE TABLE " + TABLE_PLACES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_CITY + " TEXT, " +
                    COLUMN_PHOTO_SRC + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_ADDRESS + " TEXT" +
                    ");";

    private static final String TABLE_CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LOGIN + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT" +
                    ");";

    private static final String TABLE_CREATE_USERS_PLACES =
            "CREATE TABLE " + TABLE_USERS_PLACES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_PLACE_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_PLACE_ID + ") REFERENCES " + TABLE_PLACES + "(" + COLUMN_ID + ")" +
                    ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_PLACES);
        db.execSQL(TABLE_CREATE_USERS);
        db.execSQL(TABLE_CREATE_USERS_PLACES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_PLACES + " ADD COLUMN " + COLUMN_PHOTO_SRC + " TEXT;");
        }
        if (oldVersion < 3) {
            db.execSQL(TABLE_CREATE_USERS);
        }
        if (oldVersion < 4) {
            db.execSQL(TABLE_CREATE_USERS_PLACES);
        }
    }

    public long insertPlace(String name, String city, String description, String photoSrc, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CITY, city);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_PHOTO_SRC, photoSrc);
        values.put(COLUMN_ADDRESS, address);

        long result = db.insert(TABLE_PLACES, null, values);
        db.close();

        return result;
    }

    public void deleteAllPlaces() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLACES, null, null);
        db.close();
    }

    public long insertUser(String login, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, login);
        values.put(COLUMN_PASSWORD, hashPassword(password));

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result;
    }

    public boolean checkUser(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_LOGIN + "=?";
        String[] selectionArgs = {login};

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PASSWORD}, selection, selectionArgs, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                int passwordColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_PASSWORD);
                String storedHash = cursor.getString(passwordColumnIndex);
                return checkPassword(password, storedHash);
            } else {
                return false;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean isLoginExists(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_LOGIN + " = ?";
        String[] selectionArgs = {login};

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, selection, selectionArgs, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();

        return exists;
    }

    public long insertUserPlace(int userId, int placeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_PLACE_ID, placeId);

        long result = db.insert(TABLE_USERS_PLACES, null, values);
        db.close();

        return result;
    }

    public int getUserId(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_LOGIN + " = ?",
                new String[]{login}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            cursor.close();
        }

        db.close();

        return userId;
    }

    public void deleteUserPlace(int userId, int placeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_USER_ID + " = ? AND " + COLUMN_PLACE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(placeId)};
        db.delete(TABLE_USERS_PLACES, selection, selectionArgs);
        db.close();
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private boolean checkPassword(String password, String storedHash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);
        return result.verified;
    }

    public Place getPlaceById(int placeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_CITY,
                COLUMN_PHOTO_SRC,
                COLUMN_DESCRIPTION,
                COLUMN_ADDRESS
        };

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(placeId)};

        Cursor cursor = db.query(
                TABLE_PLACES,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String city = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY));
            String photoSrc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_SRC));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));

            cursor.close();
            db.close();

            return new Place(id, name, city, photoSrc, description, address);
        } else {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return null;
        }
    }

    public boolean isPlaceLovedByUser(int userId, int placeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {"1"};
        String selection = COLUMN_USER_ID + " = ? AND " + COLUMN_PLACE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(placeId)};

        Cursor cursor = db.query(
                TABLE_USERS_PLACES,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean isLoved = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return isLoved;
    }

    public List<Place> getAllPlaces() {
        List<Place> places = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_CITY,
                COLUMN_PHOTO_SRC,
                COLUMN_DESCRIPTION,
                COLUMN_ADDRESS
        };

        Cursor cursor = db.query(TABLE_PLACES, columns, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY));
                String photoSrc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_SRC));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                places.add(new Place(id, name, city, photoSrc, description, address));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return places;
    }

    public List<Integer> getUserLovedPlaceIds(int userId) {
        List<Integer> lovedPlaceIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String selectQuery = "SELECT place_id FROM UsersPlaces WHERE user_id = ?";

        try {
            cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("place_id");
                if (columnIndex != -1) {
                    do {
                        int placeId = cursor.getInt(columnIndex);
                        lovedPlaceIds.add(placeId);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return lovedPlaceIds;
    }

    public List<Place> getPlacesByIds(List<Integer> placeIds) {
        List<Place> places = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            for (int placeId : placeIds) {
                String selectQuery = "SELECT * FROM Place WHERE id = ?";
                cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(placeId)});
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
                    String photoSrc = cursor.getString(cursor.getColumnIndexOrThrow("photo_src")); // Получаем путь к фотографии
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    Place place = new Place(placeId, name, city, photoSrc, description, address); // Создаем объект Place с путем к фотографии
                    places.add(place);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return places;
    }

    public void deletePlace(int placeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Place", "id = ?", new String[]{String.valueOf(placeId)});
        db.delete("UsersPlaces", "place_id = ?", new String[]{String.valueOf(placeId)});
        db.close();
    }

    public long updatePlace(Place place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, place.getName());
        values.put(COLUMN_CITY, place.getCity());
        values.put(COLUMN_DESCRIPTION, place.getDescription());
        values.put(COLUMN_PHOTO_SRC, place.getPhotoSrc());
        values.put(COLUMN_ADDRESS, place.getAddress());
        long result = db.update(TABLE_PLACES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(place.getId())});
        db.close();
        return result;
    }
}
