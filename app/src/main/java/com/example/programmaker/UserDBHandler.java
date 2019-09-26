package com.example.programmaker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UserDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserDB.db";
    public static final String TABLE_NAME = "User";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_PASSWORD = "Password";
    public static final String COLUMN_FIRST_NAME = "FirstName";
    public static final String COLUMN_LAST_NAME = "LastName";

    public UserDBHandler(@Nullable Context context,
                         @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement that creates the table
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
                COLUMN_EMAIL + " TEXT PRIMARYKEY, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT )";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public User findHandler(String userEmail) {
        boolean ok = false;
        User user = new User();
        // SQL statement to fetch User record
        String query = "SELECT * FROM " + TABLE_NAME +
                       " WHERE " + COLUMN_EMAIL + "='" + userEmail + "';";

        // Hold the result of the query in a cursor
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            user.setEmail(cursor.getString(0));
            user.setPassword(cursor.getString(1));
            user.setFirstName(cursor.getString(2));
            user.setLastName(cursor.getString(3));
        } else {
            user = null;
        }
        db.close();
        return user;
    }

    public boolean addHandler(User user) {
        boolean ok = true;

        if ( findHandler(user.getEmail()) == null ) {
            // Content values holds the information for the table
            ContentValues values = new ContentValues();
            values.put(COLUMN_EMAIL, user.getEmail());
            values.put(COLUMN_PASSWORD, user.getPassword());
            values.put(COLUMN_FIRST_NAME, user.getFirstName());
            values.put(COLUMN_LAST_NAME, user.getLastName());
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_NAME, null, values);
            db.close();
            System.out.println("Added user with email: " + user.getEmail() +
                    "pass: " + user.getPassword() +
                    "firstName: " + user.getFirstName() +
                    "lastName: " + user.getLastName());
        } else {
            ok = false;
            System.out.println("Email already used: " + user.getEmail());
        }

        return ok;
    }

    public boolean deleteHandler(String userEmail) {

        boolean ok = false;
        User user = findHandler(userEmail);
        if ( user != null ) {
            String query = "DELETE FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_EMAIL + "='" + userEmail + "';";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(query);
            ok = true;
            db.close();
        }

        return ok;
    }
}
