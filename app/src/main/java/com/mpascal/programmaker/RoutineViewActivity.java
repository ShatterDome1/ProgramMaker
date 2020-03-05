package com.mpascal.programmaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mpascal.programmaker.R;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.db.Exercise;
import com.mpascal.programmaker.fragments.RoutineFragment;

import java.util.ArrayList;

public class RoutineViewActivity extends AppCompatActivity {

    private EditText routineTitle;
    private Button updateRoutine;
    private Routine currentRoutine;

    private TextView exercisesBlock1Day0;
    private TextView exercisesBlock1Day1;
    private TextView exercisesBlock1Day2;
    private TextView exercisesBlock1Day3;
    private TextView exercisesBlock1Day4;
    private TextView exercisesBlock1Day5;
    private TextView exercisesBlock1Day6;

    private static final String TAG = "RoutineViewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_view);

        // Get the routine clicked
        Intent intent = getIntent();
        currentRoutine = intent.getParcelableExtra(RoutineFragment.PACKAGE_NAME + ".routine");

        routineTitle = findViewById(R.id.routine_view_title);
        routineTitle.setText(currentRoutine.getTitle());

        updateRoutine = findViewById(R.id.routine_view_update_title);
        updateRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails();
            }
        });

        // Text fields
        exercisesBlock1Day0 = findViewById(R.id.exercises_block1_day0);
        exercisesBlock1Day1 = findViewById(R.id.exercises_block1_day1);
        exercisesBlock1Day2 = findViewById(R.id.exercises_block1_day2);
        exercisesBlock1Day3 = findViewById(R.id.exercises_block1_day3);
        exercisesBlock1Day4 = findViewById(R.id.exercises_block1_day4);
        exercisesBlock1Day5 = findViewById(R.id.exercises_block1_day5);
        exercisesBlock1Day6 = findViewById(R.id.exercises_block1_day6);

        loadExercises();

    }

    private void updateDetails() {
        if (!currentRoutine.getTitle().equals(routineTitle.getText().toString())) {
            currentRoutine.setTitle(routineTitle.getText().toString());
        }
    }

    private void loadExercises() {
        // set the exercises table
        String[][] exercisesPerBlock = currentRoutine.getExercisesPerBlock();
        ArrayList<Integer> daysAvailable = currentRoutine.getDaysAvailable();

        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < daysAvailable.size(); j++) {
                Log.d(TAG, "loadExercises: exercises: " + exercisesPerBlock[i][j]);
            }
        }

        String[][] intensityPerBlock = currentRoutine.getIntensityPerBlock();
        for (int i =0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                Log.d(TAG, "loadExercises: intensitiy: " + intensityPerBlock[i][j]);
            }
        }

    }
}
