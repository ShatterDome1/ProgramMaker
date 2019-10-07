package com.example.programmaker.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import static androidx.room.OnConflictStrategy.FAIL;

@Dao
@TypeConverters(DateConverter.class)
public interface UserDao {

    @Query("SELECT * from User where User.email = :email")
    LiveData<User> findUserByEmail(String email);

    @Insert(onConflict = FAIL)
    void addUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM User")
    void deleteAll();
}
