package com.mpascal.programmaker.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.db.ExerciseDB;
import com.mpascal.programmaker.db.RoutineDB;
import com.mpascal.programmaker.repositories.ExerciseRepository;
import com.mpascal.programmaker.repositories.RoutineRepository;

import java.util.ArrayList;

public class SurveyFragmentViewModel extends ViewModel {
    private static final String TAG = "SurveyFragmentViewModel";
    private MutableLiveData<ArrayList<Routine>> routines;
    private RoutineRepository routineRepository;

    private MutableLiveData<ArrayList<ExerciseDB>> mainExercises;
    private MutableLiveData<ArrayList<ExerciseDB>> secondaryExercises;
    private MutableLiveData<ArrayList<ExerciseDB>> accessoryExercises;
    private MutableLiveData<ArrayList<ExerciseDB>> cardioExercises;
    private ExerciseRepository exercisesRepository;

    private MutableLiveData<Boolean> isAddingRoutine = new MutableLiveData<>();

    private MutableLiveData<Boolean> isFetchingData = new MutableLiveData<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void init(String email) {
        if (routines == null) {
            routineRepository = RoutineRepository.getInstance();
            routines = routineRepository.getRoutines(email, isFetchingData);
        }

        exercisesRepository = ExerciseRepository.getInstance();
        if (mainExercises == null) {
            Log.d(TAG, "init: Fetching Main Exercises from database");
            mainExercises = exercisesRepository.getExercises("Main", isFetchingData);
        }

        if (secondaryExercises == null) {
            Log.d(TAG, "init: Fetching Secondary Exercises from database");
            secondaryExercises = exercisesRepository.getExercises("Secondary", isFetchingData);
        }

        if (accessoryExercises == null) {
            Log.d(TAG, "init: Fetching Accessory Exercises from database");
            accessoryExercises = exercisesRepository.getExercises("Accessory", isFetchingData);
        }

        if (cardioExercises == null) {
            Log.d(TAG, "init: Fetching Cardio Exercises from database");
            cardioExercises = exercisesRepository.getExercises("Cardio", isFetchingData);
        }
    }

    public ArrayList<ExerciseDB> getExercises(String category) {
        ArrayList<ExerciseDB> currentExercises = new ArrayList<>();

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

    public void addRoutine(final Routine routine) {
        isAddingRoutine.postValue(true);
        final ArrayList<Routine> currentRoutines = routines.getValue();

        // Check if another routine with the same title already exists
        String uniqueTitle = getUniqueTitle(currentRoutines, routine.getTitle(), 0);

        if (!uniqueTitle.equals(routine.getTitle())) {
            routine.setTitle(uniqueTitle);
        }

        // Add the routine to the Database
        RoutineDB newRoutine = new RoutineDB(routine);
        db.collection("Routines").document(routine.getEmail() + routine.getTitle()).set(newRoutine).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: " + routine.getTitle() + " added");
                currentRoutines.add(0, routine);
                routines.postValue(currentRoutines);
                isAddingRoutine.postValue(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }

    private String getUniqueTitle (ArrayList<Routine> currentRoutines, String title, int ct) {
        boolean isUnique = true;

        String uniqueTitle = title;
        if (ct != 0) {
            uniqueTitle = title + ct;
        }

        for (RoutineDB routine : currentRoutines) {
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

    public LiveData<Boolean> getIsAddingRoutine() {
        return isAddingRoutine;
    }

    public LiveData<Boolean> getIsFetchingData() {
        return isFetchingData;
    }
}
