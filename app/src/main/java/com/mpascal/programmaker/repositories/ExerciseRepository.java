package com.mpascal.programmaker.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mpascal.programmaker.db.ExerciseDB;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * Singleton pattern
 */
public class ExerciseRepository {

    private static ExerciseRepository instance;
    private ArrayList<ExerciseDB> mainExercises = new ArrayList<>();
    private ArrayList<ExerciseDB> secondaryExercises = new ArrayList<>();
    private ArrayList<ExerciseDB> accessoryExercises = new ArrayList<>();
    private ArrayList<ExerciseDB> cardioExercises = new ArrayList<>();
    private boolean isMainSet = false;
    private boolean isSecondarySet = false;
    private boolean isAccessorySet = false;
    private boolean isCardioSet = false;


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static ExerciseRepository getInstance() {
        if (instance == null) {
            instance = new ExerciseRepository();
        }
        return instance;
    }

    //Get the data from the database
    public MutableLiveData<ArrayList<ExerciseDB>> getExercises(String category, MutableLiveData<Boolean> isFetchingData) {

        MutableLiveData<ArrayList<ExerciseDB>> data = new MutableLiveData<>();
        switch (category) {
            case "Main":
                if (!isMainSet) {
                    setExercises(category, isFetchingData);
                    isMainSet = true;
                }
                data.setValue(mainExercises);

                break;

            case "Secondary":
                if (!isSecondarySet) {
                    setExercises(category, isFetchingData);
                    isSecondarySet = true;
                }
                data.setValue(secondaryExercises);
                break;

            case "Accessory":
                if (!isAccessorySet) {
                    setExercises(category, isFetchingData);
                    isAccessorySet = true;
                }
                data.setValue(accessoryExercises);
                break;

            case "Cardio":
                if (!isCardioSet) {
                    setExercises(category, isFetchingData);
                    isCardioSet = true;
                }
                data.setValue(cardioExercises);
                break;
        }

        return data;
    }

    private void setExercises(final String category, final MutableLiveData<Boolean> isFetchingData) {
        isFetchingData.postValue(true);

        // Get the exercises from the database and add them to the respective arrays
        final ArrayList<ExerciseDB> exercises;
        switch (category) {
            case "Main":
                exercises = mainExercises;
                break;

            case "Secondary":
                exercises = secondaryExercises;
                break;

            case "Accessory":
                exercises = accessoryExercises;
                break;

            case "Cardio":
                exercises = cardioExercises;
                break;

            default:
                Log.d(TAG, "setExercises: Category searched for is invalid");
                exercises = new ArrayList<>();
                break;
        }

        db.collection("Exercises").whereEqualTo("category", category).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ExerciseDB exercise = documentSnapshot.toObject(ExerciseDB.class);
                    exercise.setName(documentSnapshot.getId());
                    exercises.add(exercise);
                    Log.d(TAG, "onSuccess: Fetched Exercise: " + exercise.getName() + " Category: " + exercise.getCategory());
                }
                isFetchingData.postValue(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }
}
