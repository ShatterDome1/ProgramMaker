package com.example.programmaker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.programmaker.db.UserRepository;

public class LoginViewModel extends AndroidViewModel {

    private UserRepository mRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    public void find(String userEmail) {
        mRepository.find(userEmail);
    }
}
