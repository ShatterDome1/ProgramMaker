package com.mpascal.programmaker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mpascal.programmaker.MainActivity;
import com.mpascal.programmaker.R;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.db.ExerciseDB;
import com.mpascal.programmaker.db.UserDB;
import com.mpascal.programmaker.dialogs.LoadingDialog;
import com.mpascal.programmaker.viewmodels.SurveyFragmentViewModel;

import java.util.ArrayList;

public class SurveyFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "SurveyFragment";
    
    private SurveyFragmentListener listener;

    private Spinner routineGoal;
    private String goal;

    private RadioGroup weightGroup;
    private EditText weight;

    private RadioGroup heightGroup;
    private EditText height;

    private Button createRoutine;

    private UserDB currentUser;

    private SurveyFragmentViewModel surveyFragmentViewModel;
    private ViewModelProvider viewModelProvider;

    private LoadingDialog loadingDialog;

    // This interface is implemented by the MainActivity, the answers from the survey will go there
    // then to the RoutineFragment where the routine will be created
    public interface SurveyFragmentListener {
        boolean onSurveyCompleted(String goal, String weight, String height);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey, container, false);

        loadingDialog = new LoadingDialog(getActivity());

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

        // Get current user from bundle
        Bundle bundle = getArguments();
        currentUser = bundle.getParcelable(MainActivity.PACKAGE_NAME + ".userDetails");

        // Initialse view model
        viewModelProvider = new ViewModelProvider(this);
        surveyFragmentViewModel = viewModelProvider.get(SurveyFragmentViewModel.class);

        surveyFragmentViewModel.init(currentUser.getEmail());

        surveyFragmentViewModel.getIsFetchingData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    loadingDialog.showLoadingDialog();
                    Log.d(TAG, "onChanged: Fetching exercises from database");
                } else {
                    loadingDialog.dismissLoadingDialog();
                }
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
                    Toast.makeText(getActivity(), "For Ft please use ?'? format", Toast.LENGTH_SHORT).show();
                }
            }

            if (heightUnit.equals("Cm")) {
                // Check that both strings are numbers
                try {
                    Integer.parseInt(heightStr);
                    heightStr += heightUnit;
                } catch (NumberFormatException nfe) {
                    isOk = false;
                    Toast.makeText(getActivity(), "For Cm please add your height as a number", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Please enter your height", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Please enter your weight", Toast.LENGTH_SHORT).show();
        }

        if (isOk) {
            // Calls onSurveyComplete in MainActivity which in turn calls addRoutine from this fragment
            isOk = listener.onSurveyCompleted(goal, weightStr, heightStr);
        }

        if (isOk) {
            surveyFragmentViewModel.getIsAddingRoutine().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        loadingDialog.showLoadingDialog();
                        Log.d(TAG, "onChanged: In progress of adding routine");
                    } else {
                        loadingDialog.dismissLoadingDialog();
                        RoutineFragment routineFragment = new RoutineFragment();
                        Bundle bundle = getArguments();
                        routineFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                routineFragment).commit();
                    }
                }
            });
        }
    }

    public void addRoutine(String goal, ArrayList<Integer> daysAvailable, String weight, String height, int age) {
        // Get copies of the exercise lists, if we don't get a copy we pass the lists as reference to the new routine object
        // which will affect the exercise lists values in the repository if a delete/add operation is made
        ArrayList<ExerciseDB> mainExercises = new ArrayList<>(surveyFragmentViewModel.getExercises("Main"));
        ArrayList<ExerciseDB> secondaryExercises = new ArrayList<>(surveyFragmentViewModel.getExercises("Secondary"));
        ArrayList<ExerciseDB> accessoryExercises = new ArrayList<>(surveyFragmentViewModel.getExercises("Accessory"));
        ArrayList<ExerciseDB> cardioExercises = new ArrayList<>(surveyFragmentViewModel.getExercises("Cardio"));

        // first param of the Routine object is Title which will be defaulted to the goal
        // Default bmi, routine split, intensityPerBLockStr and exercisesPerBlockStr to empty
        // as they will be determined in the class
        Log.d(TAG, "addRoutine: Hypertrophy");
        surveyFragmentViewModel.addRoutine(new Routine(goal,
                goal,
                daysAvailable,
                0.0,
                "",
                age,
                "",
                "",
                currentUser.getEmail(),
                weight,
                height,
                mainExercises,
                secondaryExercises,
                accessoryExercises,
                cardioExercises));
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
