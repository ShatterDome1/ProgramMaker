package com.mpascal.programmaker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mpascal.programmaker.R;

public class ChangeEmailDialog extends DialogFragment {
    private EditText password;
    private EditText newEmail;

    private ChangeEmailDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ChangeEmailDialog.ChangeEmailDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ChangeEmailDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_email, null);

        newEmail = view.findViewById(R.id.new_email);
        password = view.findViewById(R.id.change_email_password);

        builder.setView(view).setTitle("Type in new email and current password").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newEmailStr = newEmail.getText().toString();
                String passwordStr = password.getText().toString();

                listener.changeEmail(newEmailStr, passwordStr);
            }
        });

        return builder.create();
    }

    public interface ChangeEmailDialogListener {
        void changeEmail(String newEmail, String password);
    }
}
