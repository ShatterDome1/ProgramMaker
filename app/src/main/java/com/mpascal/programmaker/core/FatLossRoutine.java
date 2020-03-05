package com.mpascal.programmaker.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.mpascal.programmaker.db.Exercise;

import java.util.ArrayList;

public class FatLossRoutine extends Routine {

    public FatLossRoutine(String title,
                          String goal,
                          ArrayList<Integer> daysAvailable,
                          String weight, String height,
                          int age,
                          ArrayList<Exercise> mainExercises,
                          ArrayList<Exercise> secondaryExercises,
                          ArrayList<Exercise> accessoryExercises,
                          ArrayList<Exercise> cardioExercises) {
        super(title, goal, daysAvailable, weight, height, age, mainExercises, secondaryExercises, accessoryExercises, cardioExercises);
    }

    @Override
    public String[][] calcIntensityPerBlocks() {
        return new String[0][];
    }

    protected FatLossRoutine(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<FatLossRoutine> CREATOR = new Parcelable.Creator<FatLossRoutine>() {
        @Override
        public FatLossRoutine createFromParcel(Parcel source) {
            return new FatLossRoutine(source);
        }

        @Override
        public FatLossRoutine[] newArray(int size) {
            return new FatLossRoutine[size];
        }
    };


}
