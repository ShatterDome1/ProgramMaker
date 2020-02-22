package com.mpascal.programmaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mpascal.programmaker.R;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.fragments.RoutineFragment;

public class RoutineViewActivity extends AppCompatActivity {

    private EditText routineTitle;
    private Button updateRoutine;
    private Routine currentRoutine;


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
    }

    private void updateDetails() {
        if (!currentRoutine.getTitle().equals(routineTitle.getText().toString())) {
            currentRoutine.setTitle(routineTitle.getText().toString());
        }
    }
}
