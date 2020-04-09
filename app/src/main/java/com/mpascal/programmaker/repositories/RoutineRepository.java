package com.mpascal.programmaker.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.db.RoutineDB;

import java.util.ArrayList;

/**
 * Singleton pattern
 */
public class RoutineRepository {

    private static RoutineRepository instance;
    private ArrayList<Routine> dataSet = new ArrayList<>();
    private boolean isRoutinesSet = false;

    private static final String TAG = "RoutineRepository";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static RoutineRepository getInstance() {
        if(instance == null) {
            instance = new RoutineRepository();
        }
        return instance;
    }

    // Make the default constructor private for singleton pattern
    private RoutineRepository() {}

    // Pretend to get data from database
    public MutableLiveData<ArrayList<Routine>> getRoutines(String email, MutableLiveData<Boolean> isFetchingData) {
        // check if the routines have already been fetched
        if (!isRoutinesSet) {
            setRoutines(email, isFetchingData);
            isRoutinesSet = true;
        }

        MutableLiveData<ArrayList<Routine>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        Log.d(TAG, "getRoutines: " + data.getValue().size());
        return data;
    }

    private void setRoutines(final String email, final MutableLiveData<Boolean> isFetchingData) {
        isFetchingData.postValue(true);

        // Get the routines from the database
        db.collection("Routines").whereEqualTo("email", email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        RoutineDB routine = documentSnapshot.toObject(RoutineDB.class);
                        dataSet.add(new Routine(routine.getTitle(),
                                routine.getGoal(),
                                routine.getDaysAvailable(),
                                routine.getBmi(),
                                routine.getRoutineSplit(),
                                routine.getAge(),
                                routine.getIntensityPerBlockStr(),
                                routine.getExercisesPerBlockStr(),
                                email));
                        Log.d(TAG, "onSuccess: " + routine.getTitle() + " retrieved for email " + email);
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

    public void clearData() {
        Log.d(TAG, "clearData: remove data on logout");
        dataSet.clear();
        isRoutinesSet = false;
    }
}
