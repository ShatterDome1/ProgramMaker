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

public class ForgotPasswordDialog extends DialogFragment {
    private EditText email;
    ForgotPasswordDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ForgotPasswordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ChangePasswordDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_forgot_password, null);

        email = view.findViewById(R.id.forgot_pass_email);

        builder.setView(view).setTitle("Forgot Password").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailStr = email.getText().toString();

                listener.sendForgotPasswordEmail(emailStr);
            }
        });

        return builder.create();
    }

    public interface ForgotPasswordDialogListener {
        void sendForgotPasswordEmail(String email);
    }
}
