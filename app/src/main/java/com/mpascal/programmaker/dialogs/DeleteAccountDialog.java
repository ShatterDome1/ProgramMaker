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

public class DeleteAccountDialog extends DialogFragment {
    private EditText password;

    private DeleteAccountDialog.DeleteAccountDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteAccountDialog.DeleteAccountDialogListener) context;
        } catch (ClassCastException e ) {
            throw new ClassCastException(context.toString() + " must implement DeleteAccountDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_account, null);

        password = view.findViewById(R.id.delete_account_password);

        builder.setView(view).setTitle("Type in password to delete account").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passwordStr = password.getText().toString();

                listener.deleteAccount(passwordStr);
            }
        });

        return builder.create();
    }

    public interface DeleteAccountDialogListener {
        void deleteAccount(String password);
    }
}
