package com.mpascal.programmaker.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.mpascal.programmaker.db.Exercise;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Routine implements Parcelable {

    private String title;
    private String goal;
    private ArrayList<Integer> daysAvailable;
    private Double bmi;
    private String routineSplit;
    private int age;

    public Routine(String title,
                   String goal,
                   ArrayList<Integer> daysAvailable,
                   String weight,
                   String height,
                   int age) {
        this.title = title;
        this.goal = goal;
        this.daysAvailable = daysAvailable;
        calcRoutineSplit(daysAvailable, goal);
        calcBMI(weight, height);
        this.age = age;
    }

    protected Routine (Parcel in) {
        title = in.readString();
        goal = in.readString();
        daysAvailable = in.readArrayList(Routine.class.getClassLoader());
        bmi = in.readDouble();
        routineSplit = in.readString();
        age = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(goal);
        dest.writeList(daysAvailable);
        dest.writeDouble(bmi);
        dest.writeString(routineSplit);
        dest.writeInt(age);
    }

    private void calcRoutineSplit(ArrayList<Integer> daysAvailable, String goal) {
        if (daysAvailable.size() > 3) {
            if (daysAvailable.size() == 4) {
                // Decide between Upper/Lower and Full Body
                if (goal.equals("Hypertrophy")) {
                    routineSplit = "Upper Lower";
                } else {
                    Collections.sort(daysAvailable);
                    // Check if routine has 3 odds or evens
                    int ctOdd = 0;
                    int ctEven = 0;
                    for (int i = 0; i <= daysAvailable.size() - 1; i++) {
                        if (daysAvailable.get(i) % 2 == 0) {
                            ctEven++;
                        } else {
                            ctOdd++;
                        }
                    }

                    // Check if it has 2 pairs of consecutive days
                    int ct = 0;
                    for (int i = 0; i < daysAvailable.size() - 1; i++) {
                        if (daysAvailable.get(i).equals(daysAvailable.get(i + 1) - 1)) {
                            ct++;
                        }
                    }

                    if (ct > 1 && ctOdd < 3 && ctEven < 3) {
                        routineSplit = "Upper Lower";
                    } else {
                        routineSplit = "Full Body";
                    }
                }
            } else if (daysAvailable.size() == 5) {
                routineSplit = "Upper Lower";
            } else {
                routineSplit = "Push Pull Legs";
            }
        } else {
            routineSplit = "Full Body";
        }
    }

    private void calcBMI(String weight, String height) {
        // Weight possible values: eg "42.5kg" "225lbs"
        String weightSuffix = weight.substring(weight.length() - 2);
        Double weightValue;
        if (weightSuffix.equals("Kg")) {
            weightValue = Double.parseDouble(weight.substring(0, weight.length() - 2));
        } else {
            Double pounds = Double.parseDouble(weight.substring(0, weight.length() - 3));

            // Convert to kg
            weightValue = pounds * 0.453592;
        }

        // Height possible values: eg "150cm" "7'5ft"
        String heightSuffix = height.substring(height.length() - 2);
        Double heightValue;
        if (heightSuffix.equals("Cm")) {
            Double cms = Double.parseDouble(height.substring(0, height.length() - 2));

            // Convert to meters
            heightValue = cms / 100;
        } else {
            String[] arrOfStrings = height.split("'");
            // Safe to assume that people are not over 10 feet
            int feet = Integer.parseInt(arrOfStrings[0]);

            int inches;
            if (arrOfStrings[1].charAt(1) == 'F') {
                inches = Character.getNumericValue(arrOfStrings[1].charAt(0));
            } else {
                inches = Integer.parseInt(arrOfStrings[1].substring(0, 2));
            }
            inches += 12 * feet;

            // Convert to meters
            heightValue = inches * 0.0254;
        }

        bmi = weightValue / (heightValue * heightValue);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGoal() {
        return goal;
    }

    public ArrayList<Integer> getDaysAvailable() {
        return daysAvailable;
    }

    public String getRoutineSplit() {
        return routineSplit;
    }

    public Double getBmi() {
        return bmi;
    }

    public int getAge() {
        return age;
    }

    public abstract ArrayList<Exercise> getExercises();
}
