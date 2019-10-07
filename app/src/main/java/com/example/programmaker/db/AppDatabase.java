package com.example.programmaker.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = User.class, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // Reference to the database
    private static AppDatabase INSTANCE;

    public abstract UserDao userModel();

    public static AppDatabase getInMemoryDatabase (Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                   Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class)
                           .build();
        }

        return INSTANCE;
    }

    public static AppDatabase getDatabase (Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            "ProgramMakerDB").build();
        }

        return INSTANCE;
    }
}
