package com.mpascal.programmaker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mpascal.programmaker.MainActivity;
import com.mpascal.programmaker.R;
import com.mpascal.programmaker.RoutineViewActivity;
import com.mpascal.programmaker.core.FatLossRoutine;
import com.mpascal.programmaker.core.HypertrophyRoutine;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.adapters.RoutineAdapter;
import com.mpascal.programmaker.core.StrengthRoutine;
import com.mpascal.programmaker.db.Exercise;
import com.mpascal.programmaker.db.User;
import com.mpascal.programmaker.viewmodels.RoutineFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class RoutineFragment extends Fragment {
    private static final String TAG = "RoutineFragment";
    public static final String PACKAGE_NAME = "com.mpascal.programmaker.fragments";

    private RecyclerView recyclerView;
    private static RoutineAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ViewModelProvider viewModelProvider;
    private static RoutineFragmentViewModel routineFragmentViewModel;

    private FloatingActionButton createRoutine;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routines, container, false);

        viewModelProvider = new ViewModelProvider(this);
        routineFragmentViewModel = viewModelProvider.get(RoutineFragmentViewModel.class);

        routineFragmentViewModel.init();
        routineFragmentViewModel.getRoutines().observe(getActivity(), new Observer<List<Routine>>() {
            @Override
            public void onChanged(List<Routine> routines) {
                adapter.notifyDataSetChanged();
            }
        });

        buildRecyclerView(view);

        createRoutine = view.findViewById(R.id.fragment_create_routine);
        createRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SurveyFragment()).addToBackStack(null).commit();
            }
        });

        return view;
    }

    public static void addRoutine(String goal, ArrayList<Integer> daysAvailable, String weight, String height, int age) {
        // Get copies of the exercise lists, if we don't get a copy we pass the lists as reference to the new routine object
        // which will affect the exercise lists values in the repository if a delete/add operation is made
        ArrayList<Exercise> mainExercises = new ArrayList<>(routineFragmentViewModel.getExercises("Main"));
        ArrayList<Exercise> secondaryExercises = new ArrayList<>(routineFragmentViewModel.getExercises("Secondary"));
        ArrayList<Exercise> accessoryExercises = new ArrayList<>(routineFragmentViewModel.getExercises("Accessory"));
        ArrayList<Exercise> cardioExercises = new ArrayList<>(routineFragmentViewModel.getExercises("Cardio"));

        // first param of the Routine object is Title which will be defaulted to the goal
        switch (goal) {
            case "Hypertrophy":
                Log.d(TAG, "addRoutine: Hypertrophy");
                routineFragmentViewModel.addRoutine(new HypertrophyRoutine(goal,
                        goal,
                        daysAvailable,
                        weight,
                        height,
                        age,
                        mainExercises,
                        secondaryExercises,
                        accessoryExercises,
                        cardioExercises));
                break;

            case "Fat Loss":
                Log.d(TAG, "addRoutine: Fat loss");
                routineFragmentViewModel.addRoutine(new FatLossRoutine(goal,
                        goal,
                        daysAvailable,
                        weight,
                        height,
                        age,
                        mainExercises,
                        secondaryExercises,
                        accessoryExercises,
                        cardioExercises));
                break;

            case "Strength":
                Log.d(TAG, "addRoutine: Strength");
                routineFragmentViewModel.addRoutine(new StrengthRoutine(goal,
                        goal,
                        daysAvailable,
                        weight,
                        height,
                        age,
                        mainExercises,
                        secondaryExercises,
                        accessoryExercises,
                        cardioExercises));
                break;
        }

        adapter.notifyItemInserted(0);
    }

    private void buildRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(view.getContext());
        adapter = new RoutineAdapter(routineFragmentViewModel.getRoutines().getValue());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RoutineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent= new Intent(getActivity(), RoutineViewActivity.class);
                Routine currentRoutine = routineFragmentViewModel.getRoutine(position);
                intent.putExtra( PACKAGE_NAME + ".routine", currentRoutine);
                startActivity(intent);
                Log.d(TAG, "onItemClick: " + currentRoutine.getTitle());
            }

            @Override
            public void onDeleteClick(int position) {
                Log.d(TAG, "onDeleteClick: " + position);
                routineFragmentViewModel.deleteRoutine(position);
                adapter.notifyItemRemoved(position);
            }
        });
    }

}
