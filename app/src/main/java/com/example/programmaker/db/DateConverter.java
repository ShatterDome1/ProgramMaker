package com.example.programmaker.db;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static String toDate(Long timestamp) {
        return new Date(timestamp).toString();
    }
}
