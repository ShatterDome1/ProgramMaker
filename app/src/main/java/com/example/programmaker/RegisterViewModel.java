package com.example.programmaker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.programmaker.db.UserRepository;
import com.example.programmaker.db.User;

public class RegisterViewModel extends AndroidViewModel {

    private UserRepository mRepository;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    public void insert(User user) {
        mRepository.insert(user);
    }
}
