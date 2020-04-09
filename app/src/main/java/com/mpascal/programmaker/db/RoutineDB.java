package com.mpascal.programmaker.db;

import com.mpascal.programmaker.core.Routine;

import java.util.ArrayList;

public class RoutineDB {
    private String title;
    private String goal;
    private ArrayList<Integer> daysAvailable;
    private Double bmi;
    private String routineSplit;
    private int age;
    private String email;
    private String intensityPerBlockStr;
    private String exercisesPerBlockStr;


    // This is used to store the Routine object in the online database

    public RoutineDB(String title, String goal, ArrayList<Integer> daysAvailable, Double bmi, String routineSplit, int age, String intensityPerBlockStr, String exercisesPerBlockStr, String email) {
        this.title = title;
        this.goal = goal;
        this.daysAvailable = daysAvailable;
        this.bmi = bmi;
        this.routineSplit = routineSplit;
        this.age = age;
        this.email = email;
        this.intensityPerBlockStr = intensityPerBlockStr;
        this.exercisesPerBlockStr = exercisesPerBlockStr;
    }

    public RoutineDB(Routine routine) {
        this.title = routine.getTitle();
        this.goal = routine.getGoal();
        this.daysAvailable = routine.getDaysAvailable();
        this.bmi = routine.getBmi();
        this.routineSplit = routine.getRoutineSplit();
        this.age = routine.getAge();
        this.email = routine.getEmail();
        this.intensityPerBlockStr = routine.getIntensityPerBlockStr();
        this.exercisesPerBlockStr = routine.getExercisesPerBlockStr();
    }

    public RoutineDB() {
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

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public ArrayList<Integer> getDaysAvailable() {
        return daysAvailable;
    }

    public void setDaysAvailable(ArrayList<Integer> daysAvailable) {
        this.daysAvailable = daysAvailable;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public String getRoutineSplit() {
        return routineSplit;
    }

    public void setRoutineSplit(String routineSplit) {
        this.routineSplit = routineSplit;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getIntensityPerBlockStr() {
        return intensityPerBlockStr;
    }

    public void setIntensityPerBlockStr(String intensityPerBlockStr) {
        this.intensityPerBlockStr = intensityPerBlockStr;
    }

    public String getExercisesPerBlockStr() {
        return exercisesPerBlockStr;
    }

    public void setExercisesPerBlockStr(String exercisesPerBlockStr) {
        this.exercisesPerBlockStr = exercisesPerBlockStr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
