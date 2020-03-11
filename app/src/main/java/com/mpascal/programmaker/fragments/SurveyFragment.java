package com.mpascal.programmaker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.util.NumberUtils;
import com.mpascal.programmaker.MainActivity;
import com.mpascal.programmaker.R;
import com.mpascal.programmaker.core.Routine;

import java.util.ArrayList;
import java.util.Objects;

import io.opencensus.internal.StringUtils;

public class SurveyFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private SurveyFragmentListener listener;

    private Spinner routineGoal;
    private String goal;

    private RadioGroup weightGroup;
    private EditText weight;

    private RadioGroup heightGroup;
    private EditText height;

    private Button createRoutine;

    // This interface is implemented by the MainActivity, the answers from the survey will go there
    // then to the RoutineFragment where the routine will be created
    public interface SurveyFragmentListener {
        boolean onSurveyCompleted(String goal, String weight, String height);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey, container, false);

        // Initialise spinner with choices
        routineGoal = view.findViewById(R.id.survey_goal);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.survey_fitness_goal_answer,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routineGoal.setAdapter(adapter);
        routineGoal.setOnItemSelectedListener(this);

        // Initialise weight measurement unit selector
        weightGroup = view.findViewById(R.id.survey_radio_weight);
        weight = view.findViewById(R.id.survey_weight);

        // Initialise height measurement unit selector
        heightGroup = view.findViewById(R.id.survey_radio_height);
        height = view.findViewById(R.id.survey_height);

        createRoutine = view.findViewById(R.id.survey_create_routine);
        createRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSurveyAnswers();
            }
        });

        return view;
    }

    private void sendSurveyAnswers(){
        boolean isOk = true;

        int heightRadioId = heightGroup.getCheckedRadioButtonId();
        RadioButton heightRadio = getView().findViewById(heightRadioId);

        // Check that the correct format is entered
        String heightUnit = heightRadio.getText().toString();
        String heightStr = height.getText().toString();

        if (!heightStr.isEmpty()) {
            if (heightUnit.equals("Ft")) {
                String[] heightValues = heightStr.split("'");
                // Check that both strings are numbers
                try {
                    Integer.parseInt(heightValues[0]);
                    Integer.parseInt(heightValues[1]);
                    heightStr += heightUnit;
                } catch (NumberFormatException nfe) {
                    isOk = false;
                    Toast.makeText(getActivity(), "For Ft please use ?'? format!", Toast.LENGTH_SHORT).show();
                }
            }

            if (heightUnit.equals("Cm")) {
                // Check that both strings are numbers
                try {
                    Integer.parseInt(heightStr);
                    heightStr += heightUnit;
                } catch (NumberFormatException nfe) {
                    isOk = false;
                    Toast.makeText(getActivity(), "For Cm please add your height as a number!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Please enter your height!", Toast.LENGTH_SHORT).show();
            isOk = false;
        }

        int weightRadioId = weightGroup.getCheckedRadioButtonId();
        RadioButton weightRadio = getView().findViewById(weightRadioId);

        // Check that the weight entered is not empty
        String weightUnit = weightRadio.getText().toString();
        String weightStr = weight.getText().toString();

        if (!weightStr.isEmpty()) {
            weightStr += weightUnit;
        } else {
            isOk = false;
            Toast.makeText(getActivity(), "Please enter your weight!", Toast.LENGTH_SHORT).show();
        }

        if (isOk) {
            isOk = listener.onSurveyCompleted(goal, weightStr, heightStr);
        }

        if (isOk) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RoutineFragment()).commit();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.goal = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if ( context instanceof SurveyFragmentListener ) {
            listener = (SurveyFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement SurveyFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
