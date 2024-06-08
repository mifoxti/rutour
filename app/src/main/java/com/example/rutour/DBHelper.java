package com.example.rutour;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "places.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PLACES = "Place";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_PHOTO_SRC = "photo_src";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ADDRESS = "address";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_PLACES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_CITY + " TEXT, " +
                    COLUMN_PHOTO_SRC + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_ADDRESS + " TEXT" +
                    ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_PLACES + " ADD COLUMN " + COLUMN_PHOTO_SRC + " TEXT;");
        }
    }

    public long insertPlace(String name, String city, String description, String photoSrc, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CITY, city);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_PHOTO_SRC, photoSrc);
        values.put(COLUMN_ADDRESS, address); // Сохранение адреса

        long result = db.insert(TABLE_PLACES, null, values);
        db.close();

        return result;
    }

    public void deleteAllPlaces() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLACES, null, null);
        db.close();
    }
}
