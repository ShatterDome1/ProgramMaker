package com.mpascal.programmaker.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mpascal.programmaker.R;
import com.mpascal.programmaker.RegisterActivity;

import java.util.Calendar;

public class ChangeDateOfBirthDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private Button newDateOfbirth;

    private ChangeDateOfBirthDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ChangeDateOfBirthDialog.ChangeDateOfBirthDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ChangeDateOfBirthDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_change_date_of_birth, null);

        newDateOfbirth = view.findViewById(R.id.new_date_of_birth);

        newDateOfbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(view);
            }
        });

        builder.setView(view).setTitle("Press button to select new date of birth").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newDateOfbirthStr = newDateOfbirth.getText().toString();

                listener.changeDateOfBirth(newDateOfbirthStr);
            }
        });

        return builder.create();
    }

    private void showDatePickerDialog (View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                view.getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // January is 0 so will need to do month +1 for accurate representation
        ++month;
        String date = dayOfMonth + "/" + month + "/" + year;
        newDateOfbirth.setText(date);
    }

    public interface ChangeDateOfBirthDialogListener {
        void changeDateOfBirth(String newDateOfBirth);
    }


}
