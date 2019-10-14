package com.example.programmaker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.programmaker.db.User;
import com.example.programmaker.db.UserRepository;

public class LoginViewModel extends AndroidViewModel {

    private UserRepository mRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    public User find(String userEmail, String userPassword) {
        mRepository.find(userEmail, userPassword);
        return mRepository.getSearchResult();
    }
}
