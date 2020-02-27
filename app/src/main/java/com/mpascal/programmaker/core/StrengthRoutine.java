package com.mpascal.programmaker.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.mpascal.programmaker.db.Exercise;

import java.util.ArrayList;

public class StrengthRoutine extends Routine {

    public StrengthRoutine(String title,
                           String goal,
                           ArrayList<Integer> daysAvailable,
                           String weight,
                           String height,
                           int age,
                           ArrayList<Exercise> mainExercises,
                           ArrayList<Exercise> secondaryExercises,
                           ArrayList<Exercise> accessoryExercises,
                           ArrayList<Exercise> cardioExercises) {
        super(title, goal, daysAvailable, weight, height, age, mainExercises, secondaryExercises, accessoryExercises, cardioExercises);
    }

    protected StrengthRoutine(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<StrengthRoutine> CREATOR = new Parcelable.Creator<StrengthRoutine>() {
        @Override
        public StrengthRoutine createFromParcel(Parcel source) {
            return new StrengthRoutine(source);
        }

        @Override
        public StrengthRoutine[] newArray(int size) {
            return new StrengthRoutine[size];
        }
    };

}
