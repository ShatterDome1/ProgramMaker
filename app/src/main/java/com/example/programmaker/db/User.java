package com.example.programmaker.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password")
    @NonNull
    public String password;

    @ColumnInfo(name = "first_name")
    @NonNull
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @ColumnInfo(name = "date_of_birth")
    @NonNull
    @TypeConverters(DateConverter.class)
    public String dateOfBirth;
}
