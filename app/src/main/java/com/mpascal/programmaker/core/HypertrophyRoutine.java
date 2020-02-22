package com.mpascal.programmaker.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.mpascal.programmaker.db.Exercise;

import java.util.ArrayList;

public class HypertrophyRoutine extends Routine {

    public HypertrophyRoutine(String title, String goal, ArrayList<Integer> daysAvailable, String weight, String height, int age) {
        super(title, goal, daysAvailable, weight, height, age);
    }

    protected HypertrophyRoutine (Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<HypertrophyRoutine> CREATOR = new Parcelable.Creator<HypertrophyRoutine>() {
        @Override
        public HypertrophyRoutine createFromParcel(Parcel source) {
            return new HypertrophyRoutine(source);
        }

        @Override
        public HypertrophyRoutine[] newArray(int size) {
            return new HypertrophyRoutine[0];
        }
    };

    @Override
    public ArrayList<Exercise> getExercises() {
        return null;
    }
}
