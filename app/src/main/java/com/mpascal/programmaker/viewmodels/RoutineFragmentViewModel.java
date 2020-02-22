package com.mpascal.programmaker.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.repositories.RoutineRepository;

import java.util.ArrayList;

public class RoutineFragmentViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Routine>> routines;
    private RoutineRepository repository;

    public LiveData<ArrayList<Routine>> getRoutines() {
        return routines;
    }

    public void init() {
        if (routines != null) {
            return;
        }
        repository = RoutineRepository.getInstance();
        routines = repository.getRoutines();
    }

    public void addRoutine(final Routine routine) {
        ArrayList<Routine> currentRoutines = routines.getValue();
        currentRoutines.add(0, routine);
        routines.postValue(currentRoutines);
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
}
