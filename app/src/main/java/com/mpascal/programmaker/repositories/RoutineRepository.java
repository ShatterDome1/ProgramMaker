package com.mpascal.programmaker.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mpascal.programmaker.core.HypertrophyRoutine;
import com.mpascal.programmaker.core.Routine;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Singleton pattern
 */
public class RoutineRepository {

    private static RoutineRepository instance;
    private ArrayList<Routine> dataSet = new ArrayList<>();
    private boolean isRoutinesSet = false;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static RoutineRepository getInstance() {
        if(instance == null) {
            instance = new RoutineRepository();
        }
        return instance;
    }

    // Pretend to get data from database
    public MutableLiveData<ArrayList<Routine>> getRoutines() {
        // check if the routines have already been fetched
        if (!isRoutinesSet) {
            setRoutines();
            isRoutinesSet = true;
        }

        MutableLiveData<ArrayList<Routine>> data = new MutableLiveData<>();
        data.setValue(dataSet);

        return data;
    }

    private void setRoutines() {
        // get the routines from the db
        ArrayList<Integer> daysAvailable = new ArrayList<>(Arrays.asList(0,2,4));

        dataSet.add(new HypertrophyRoutine("Hypertrophy", "Hypertrophy", daysAvailable, "95Kg", "180Cm", 20));
    }
}
