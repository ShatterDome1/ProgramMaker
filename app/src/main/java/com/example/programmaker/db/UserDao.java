package com.example.programmaker.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.FAIL;

@Dao
public interface UserDao {

    @Query("SELECT * from User where User.email = :email")
    User findUserByEmail(String email);

    @Insert(onConflict = FAIL)
    void addUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM User")
    void deleteAll();
}
