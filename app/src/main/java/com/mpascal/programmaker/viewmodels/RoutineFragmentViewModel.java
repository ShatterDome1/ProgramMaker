package com.mpascal.programmaker.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.db.Exercise;
import com.mpascal.programmaker.repositories.ExerciseRepository;
import com.mpascal.programmaker.repositories.RoutineRepository;

import java.util.ArrayList;

public class RoutineFragmentViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Routine>> routines;
    private RoutineRepository routineRepository;

    private MutableLiveData<ArrayList<Exercise>> mainExercises;
    private MutableLiveData<ArrayList<Exercise>> secondaryExercises;
    private MutableLiveData<ArrayList<Exercise>> accessoryExercises;
    private MutableLiveData<ArrayList<Exercise>> cardioExercises;
    private ExerciseRepository exercisesRepository;

    public LiveData<ArrayList<Routine>> getRoutines() {
        return routines;
    }

    public void init() {
        if (routines != null) {
            return;
        }
        routineRepository = RoutineRepository.getInstance();
        routines = routineRepository.getRoutines();

        if (mainExercises != null || secondaryExercises !=null || accessoryExercises != null) {
            return;
        }
        exercisesRepository = ExerciseRepository.getInstance();
        mainExercises = exercisesRepository.getExercises("Main");
        secondaryExercises = exercisesRepository.getExercises("Secondary");
        accessoryExercises = exercisesRepository.getExercises("Accessory");
        cardioExercises = exercisesRepository.getExercises("Cardio");
    }

    public void addRoutine(final Routine routine) {
        ArrayList<Routine> currentRoutines = routines.getValue();

        // Check if another routine with the same title already exists
        String uniqueTitle = getUniqueTitle(currentRoutines, routine.getTitle(), 0);

        if (!uniqueTitle.equals(routine.getTitle())) {
            routine.setTitle(uniqueTitle);
        }

        currentRoutines.add(0, routine);
        routines.postValue(currentRoutines);
    }

    private String getUniqueTitle (ArrayList<Routine> currentRoutines, String title, int ct) {
        boolean isUnique = true;

        String uniqueTitle = title;
        if (ct != 0) {
            uniqueTitle = title + ct;
        }

        for (Routine routine : currentRoutines) {
            if (routine.getTitle().equals(uniqueTitle)) {
                isUnique = false;
                break;
            }
        }

        if (isUnique) {
            return uniqueTitle;
        } else {
            return getUniqueTitle(currentRoutines, title, ++ct);
        }
    }

    public void deleteRoutine(final int position) {
        ArrayList<Routine> currentRoutines = routines.getValue();
        currentRoutines.remove(position);
        routines.postValue(currentRoutines);
    }

    public Routine getRoutine(final int position) {
        ArrayList<Routine> currentRoutines = routines.getValue();
        return currentRoutines.get(position);
    }

    public ArrayList<Exercise> getExercises(String category) {
        ArrayList<Exercise> currentExercises = new ArrayList<>();

        // Current Exercises should never be empty
        switch (category) {
            case "Main":
                currentExercises = mainExercises.getValue();
                break;

            case "Secondary":
                currentExercises = secondaryExercises.getValue();
                break;

            case "Accessory":
                currentExercises = accessoryExercises.getValue();
                break;

            case "Cardio":
                currentExercises = cardioExercises.getValue();
                break;
        }

        return currentExercises;
    }
}
