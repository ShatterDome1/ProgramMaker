package com.mpascal.programmaker.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mpascal.programmaker.adapters.RoutineAdapter;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.db.RoutineDB;
import com.mpascal.programmaker.repositories.RoutineRepository;

import java.util.ArrayList;

public class RoutineFragmentViewModel extends ViewModel {

    private static final String TAG = "RoutineFragmentViewModel";

    private MutableLiveData<ArrayList<Routine>> routines;
    private RoutineRepository routineRepository;

    private MutableLiveData<Boolean> isFetchingData = new MutableLiveData<>();

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

    public void deleteRoutine(final String userEmail, final int position, final RoutineAdapter adapter) {
        final ArrayList<Routine> currentRoutines = routines.getValue();
        // Delete the Routine from the database
        final RoutineDB routine = new RoutineDB(currentRoutines.get(position));

        db.collection("Routines").document(userEmail + routine.getTitle()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: " + routine.getTitle() + " deleted");
                currentRoutines.remove(position);
                routines.postValue(currentRoutines);
                adapter.notifyItemRemoved(position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }

    public Routine getRoutine(final int position) {
        ArrayList<Routine> currentRoutines = routines.getValue();
        return currentRoutines.get(position);
    }

    public LiveData<Boolean> getIsFetchingData() {
        return isFetchingData;
    }

    public void clearData() {
        routineRepository.clearData();
    }
}
