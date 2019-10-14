package com.example.programmaker.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity
public class User {

    @ColumnInfo(name = "first_name")
    @NonNull
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password")
    @NonNull
    public String password;

    @ColumnInfo(name = "date_of_birth")
    @NonNull
    public String dateOfBirth;

    public User(@NonNull String firstName,
                String lastName,
                @NonNull String email,
                @NonNull String password,
                @NonNull String dateOfBirth) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }
}
