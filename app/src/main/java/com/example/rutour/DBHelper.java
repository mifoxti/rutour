package com.example.rutour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "places.db";
    private static final int DATABASE_VERSION = 3; // Увеличиваем версию базы данных

    public static final String TABLE_PLACES = "Place";
    public static final String TABLE_USERS = "Users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_PHOTO_SRC = "photo_src";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_CREATE =
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

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_CREATE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_PLACES + " ADD COLUMN " + COLUMN_PHOTO_SRC + " TEXT;");
        }
        if (oldVersion < 3) {
            db.execSQL(TABLE_CREATE_USERS);
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

    // Insert new user with hashed password
    public long insertUser(String login, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, login);
        values.put(COLUMN_PASSWORD, hashPassword(password)); // Hash the password

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result;
    }

    // Check if user exists with hashed password
    public boolean checkUser(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_LOGIN + "=?";
        String[] selectionArgs = {login};

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PASSWORD}, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String storedHash = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            cursor.close();
            db.close();
            return checkPassword(password, storedHash); // Check the password against the stored hash
        } else {
            cursor.close();
            db.close();
            return false;
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

    // Hash password using BCrypt
    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    // Check password using BCrypt
    private boolean checkPassword(String password, String storedHash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);
        return result.verified;
    }
}
