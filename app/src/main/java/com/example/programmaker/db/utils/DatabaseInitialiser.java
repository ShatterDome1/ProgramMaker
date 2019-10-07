package com.example.programmaker.db.utils;

import android.os.AsyncTask;

import com.example.programmaker.db.AppDatabase;
import com.example.programmaker.db.User;

import java.util.Date;

public class DatabaseInitialiser {
    private static void addUser(final AppDatabase db,
                           final String email,
                           final String password,
                           final String firstName,
                           final String lastName,
                           final Date dateOfBirth) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.firstName = firstName;
        user.lastName = lastName;
        user.dateOfBirth = dateOfBirth.toString();

        db.userModel().addUser(user);
    }

    private static void populateWithTestData (AppDatabase db) {
        db.userModel().deleteAll();

        Date date1 = new Date(19971218);
        Date date2 = new Date(19960623);
        addUser(db, "michel", "pascal", "Michel", "Pascal", date1);
        addUser(db, "jeff", "pascal", "Jean", "Pascal", date2);
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }
    }
}
