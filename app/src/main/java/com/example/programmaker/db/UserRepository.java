package com.example.programmaker.db;

import android.app.Application;
import android.os.AsyncTask;

public class UserRepository {
    private UserDao mUserDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mUserDao = db.userModel();
    }

    // Insert API for database
    public void insert (User user) {
        new insertAsyncTask(mUserDao).execute(user);
    }

    private static class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        insertAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }


        @Override
        protected Void doInBackground(User... users) {
            mAsyncTaskDao.addUser(users[0]);
            return null;
        }
    }

    // Find API for database
    public void find (String email) {
        new findAsyncTask(mUserDao).execute(email);
    }

    private static class findAsyncTask extends AsyncTask<String, Void, Void> {

        private UserDao mAsyncTaskDao;

        findAsyncTask(UserDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mAsyncTaskDao.findUserByEmail(strings[0]);
            return null;
        }
    }
}
