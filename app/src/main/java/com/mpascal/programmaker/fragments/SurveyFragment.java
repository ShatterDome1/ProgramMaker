package com.mpascal.programmaker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mpascal.programmaker.MainActivity;
import com.mpascal.programmaker.R;
import com.mpascal.programmaker.core.Routine;

public class SurveyFragment extends Fragment {
    private SurveyFragmentListener listener;
    private EditText routineTitle;
    private EditText routineDescription;
    private Button send;

    public interface SurveyFragmentListener {
        void onSurveyCompleted(String title, String description);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey, container, false);

        routineTitle = view.findViewById(R.id.routine_title);
        routineDescription = view.findViewById(R.id.routine_description);
        send = view.findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
        return view;
    }

    private void sendData(){
        String routineTitleStr = routineTitle.getText().toString();
        String routineDescStr = routineDescription.getText().toString();

        MainActivity reference = (MainActivity) getActivity();
        // Send data to Main Activity
        listener.onSurveyCompleted(routineTitleStr, routineDescStr);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                reference.getRoutineFragment()).commit();
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
