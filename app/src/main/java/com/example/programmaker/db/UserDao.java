package com.example.programmaker.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.ABORT;

@Dao
public interface UserDao {

    @Query("SELECT * from User where User.email = :email and User.password = :password")
    User findUserByEmail(String email, String password);

    @Insert(onConflict = ABORT)
    void addUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM User")
    void deleteAll();
}
