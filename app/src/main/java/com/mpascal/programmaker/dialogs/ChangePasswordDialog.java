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

public class ChangePasswordDialog extends DialogFragment {
    private EditText password;
    private EditText newPassword;
    private EditText newPasswordConf;

    private ChangePasswordDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ChangePasswordDialogListener) context;
        } catch (ClassCastException e ) {
            throw new ClassCastException(context.toString() + " must implement ChangePasswordDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);

        password = view.findViewById(R.id.change_pass_password);
        newPassword = view.findViewById(R.id.new_password);
        newPasswordConf = view.findViewById(R.id.new_password_conf);

        builder.setView(view).setTitle("Change Password").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passwordStr = password.getText().toString();
                String newPassStr = newPassword.getText().toString();
                String newPassStrConf = newPasswordConf.getText().toString();

                listener.changePassword(passwordStr, newPassStr, newPassStrConf);
            }
        });

        return builder.create();
    }

    public interface ChangePasswordDialogListener {
        void changePassword(String password, final String newPassStr, String newPassConfStr);
    }
}
