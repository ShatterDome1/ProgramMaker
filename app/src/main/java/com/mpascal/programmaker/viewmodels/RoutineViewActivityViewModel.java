package com.mpascal.programmaker.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.db.RoutineDB;
import com.mpascal.programmaker.repositories.RoutineRepository;

import java.util.ArrayList;

public class RoutineViewActivityViewModel extends ViewModel {

    private static final String TAG = "RoutineFragmentViewModel";

    private MutableLiveData<ArrayList<Routine>> routines;
    private RoutineRepository routineRepository;

    private MutableLiveData<Boolean> isFetchingData = new MutableLiveData<>();

    // 0 -> updating
    // 1 -> success
    // 2 -> deletion failed
    // 3 -> replace failed
    // 4 -> title already used
    private MutableLiveData<Integer> isUpdatingRoutine = new MutableLiveData<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<ArrayList<Routine>> getRoutines() {
        return routines;
    }

    public void init(String email) {
        if (routines != null) {
            return;
        }
        routineRepository = RoutineRepository.getInstance();

        routines = routineRepository.getRoutines(email, isFetchingData);
    }

    public void updateRoutine(final Routine routine, final String newTitle, final int routinePosition) {
        final ArrayList<Routine> currentRoutines = routines.getValue();

        isUpdatingRoutine.postValue(0);

        // Check if a routine with the same name already exists
        for (Routine existingRoutine : currentRoutines) {
            if (existingRoutine.getTitle().equals(newTitle)) {
                isUpdatingRoutine.postValue(4);
                return;
            }
        }

        // We first need to remove the current routine from the database, and add the new one with the updated id
        db.collection("Routines").document(routine.getEmail() + routine.getTitle()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Deletion from database successful. Add new routine.
                routine.setTitle(newTitle);
                RoutineDB updatedRoutine = new RoutineDB(routine);

                db.collection("Routines").document(routine.getEmail() + routine.getTitle()).set(updatedRoutine).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Overwrite the old routine with the new title
                        currentRoutines.set(routinePosition, routine);
                        routines.postValue(currentRoutines);
                        isUpdatingRoutine.postValue(1);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isUpdatingRoutine.postValue(3);
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isUpdatingRoutine.postValue(2);
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }

    public LiveData<Boolean> getIsFetchingData() {
        return isFetchingData;
    }

    public LiveData<Integer> getIsUpdatingRoutine() {
        return isUpdatingRoutine;
    }
}
