package com.mpascal.programmaker.fragments;

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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mpascal.programmaker.LoginActivity;
import com.mpascal.programmaker.MainActivity;
import com.mpascal.programmaker.R;
import com.mpascal.programmaker.db.UserDB;
import com.mpascal.programmaker.dialogs.ChangePasswordDialog;
import com.mpascal.programmaker.dialogs.DeleteAccountDialog;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private EditText firstName;
    private EditText lastName;
    private Button updateDetailsButton;
    private Button changePasswordButton;
    private Button deleteAccount;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get the Bundle given by the Main Activity
        Bundle bundle = getArguments();
        final UserDB user = bundle.getParcelable(MainActivity.PACKAGE_NAME + ".userDetails");

        firstName = view.findViewById(R.id.profileFirstName);
        lastName = view.findViewById(R.id.profileLastName);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());

        updateDetailsButton = view.findViewById(R.id.updateDetails);
        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetails(user);
            }
        });

        changePasswordButton = view.findViewById(R.id.change_password);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog changePassword = new ChangePasswordDialog();
                changePassword.show(getFragmentManager(), "ChangePassword");
            }
        });

        deleteAccount = view.findViewById(R.id.delete_account);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteAccountDialog deleteAccount = new DeleteAccountDialog();
                deleteAccount.show(getFragmentManager(), "DeleteAccount");
            }
        });

        return view;
    }

    private void updateDetails(UserDB user) {
        final String firstNameStr = firstName.getText().toString();
        final String lastNameStr = lastName.getText().toString();

        boolean isFirstNameChanged = false;
        boolean isLastNameChanged = false;

        Map<String, Object> updateFields = new HashMap<>();

        if (!firstNameStr.equals(user.getFirstName())) {
            updateFields.put("firstName", firstNameStr);
            isFirstNameChanged = true;
        }

        if (!lastNameStr.equals(user.getLastName())) {
            updateFields.put("lastName", lastNameStr);
            isLastNameChanged = true;
        }

        if (!updateFields.isEmpty()) {
            db.collection("Users").document(user.getEmail()).update(updateFields)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), " Account Details updated!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

            if (isFirstNameChanged || isLastNameChanged) {
                String updatedName = firstNameStr + " " + lastNameStr;

                // Update the user logged in details
                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);

                TextView loggedInUser = headerView.findViewById(R.id.logged_in_user);
                loggedInUser.setText(updatedName);
            }

        } else {
            Toast.makeText(getActivity(), "No Changes Detected!", Toast.LENGTH_SHORT).show();
        }
    }
}
