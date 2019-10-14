package com.example.programmaker.db;

import android.app.Application;
import android.os.AsyncTask;

public class UserRepository {

    private User searchResult = null;
    private UserDao mUserDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mUserDao = db.userModel();
    }

    // Insert API for database
    public void insert (User user) {
        new insertAsyncTask(mUserDao).execute(user);
    }

    // Extract the user from the async task using the following method
    private void searchFinished(User user) {
        searchResult = user;
    }

    public User getSearchResult() {
        return searchResult;
    }

    // Find API for database
    public void find (String userEmail) {
        findAsyncTask task = new findAsyncTask(mUserDao);
        task.delegate = this;
        task.execute(userEmail);
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

    private static class findAsyncTask extends AsyncTask<String, Void, User> {

        private UserDao mAsyncTaskDao;
        private UserRepository delegate=null;

        findAsyncTask(UserDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected User doInBackground(String... strings) {
            return mAsyncTaskDao.findUserByEmail(strings[0]);
        }

        @Override
        protected void onPostExecute(User result){
            delegate.searchFinished(result);
        }
    }
}
