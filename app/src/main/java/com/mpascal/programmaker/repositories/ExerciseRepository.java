package com.mpascal.programmaker.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mpascal.programmaker.db.Exercise;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * Singleton pattern
 */
public class ExerciseRepository {

    private static ExerciseRepository instance;
    private ArrayList<Exercise> mainExercises = new ArrayList<>();
    private ArrayList<Exercise> secondaryExercises = new ArrayList<>();
    private ArrayList<Exercise> accessoryExercises = new ArrayList<>();
    private ArrayList<Exercise> cardioExercises = new ArrayList<>();
    private boolean isExercisesSet = false;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static ExerciseRepository getInstance() {
        if (instance == null) {
            instance = new ExerciseRepository();
        }
        return instance;
    }

    //Get the data from the database
    public MutableLiveData<ArrayList<Exercise>> getExercises(String category) {
        if (!isExercisesSet) {
            setRoutines();
            isExercisesSet = true;
        }

        MutableLiveData<ArrayList<Exercise>> data = new MutableLiveData<>();
        switch (category) {
            case "Main":
                data.setValue(mainExercises);
                break;

            case "Secondary":
                data.setValue(secondaryExercises);
                break;

            case "Accessory":
                data.setValue(accessoryExercises);
                break;

            case "Cardio":
                data.setValue(cardioExercises);
                break;
        }

        return data;
    }

    private void setRoutines() {
        // Get the exercises from the database and add them to the respective arrays
        db.collection("Exercises").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Exercise exercise = documentSnapshot.toObject(Exercise.class);
                    exercise.setName(documentSnapshot.getId());

                    switch (exercise.getCategory()) {
                        case "Main":
                            mainExercises.add(exercise);
                            Log.d(TAG, "onSuccess: added to main " + exercise.getName());
                            break;

                        case "Secondary":
                            secondaryExercises.add(exercise);
                            Log.d(TAG, "onSuccess: added to secondary " + exercise.getName());
                            break;

                        case "Accessory":
                            accessoryExercises.add(exercise);
                            Log.d(TAG, "onSuccess: added to accessory " + exercise.getName());
                            break;

                        case "Cardio":
                            cardioExercises.add(exercise);
                            Log.d(TAG, "onSuccess: added to cardio " + exercise.getName());
                            break;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }
}
