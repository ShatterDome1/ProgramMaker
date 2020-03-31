package com.mpascal.programmaker.fragments;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mpascal.programmaker.MainActivity;
import com.mpascal.programmaker.util.AESHelper;
import com.mpascal.programmaker.LoginActivity;
import com.mpascal.programmaker.R;
import com.mpascal.programmaker.db.UserDB;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private EditText firstName;
    private EditText lastName;
    private EditText password;
    private EditText newPassword;
    private EditText newPasswordConfirmed;
    private Button   updateDetailsButton;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_profile, container, false);

        // Get the Bundle given by the Main Activity
        Bundle bundle = getArguments();
        final UserDB user = bundle.getParcelable(MainActivity.PACKAGE_NAME + ".userDetails");

        password = view.findViewById(R.id.profilePassword);
        newPassword = view.findViewById(R.id.profileNewPassword);
        newPasswordConfirmed = view.findViewById(R.id.profileNewPasswordConfirm);
        updateDetailsButton = view.findViewById(R.id.updateDetails);

        firstName = view.findViewById(R.id.profileFirstName);
        lastName = view.findViewById(R.id.profileLastName);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());

        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetails(user);
            }
        });

        return view;
    }

    private void updateDetails(UserDB user) {

        final String passwordStr = password.getText().toString();
        final String newPassStr = newPassword.getText().toString();
        final String newPassConfStr = newPasswordConfirmed.getText().toString();
        final String firstNameStr = firstName.getText().toString();
        final String lastNameStr = lastName.getText().toString();


        if ( passwordStr.equals(user.getPassword()) ) {
            boolean isLogoutNeeded = false;
            boolean isFirstNameChanged = false;
            boolean isLastNameChanged = false;

            Map<String, Object> updateFields = new HashMap<>();

            // Get encoded key from the logged in user and convert to SecretKey
            SecretKey secretKey = AESHelper.convertStringToSecretKey(user.getKey());

            if ( !firstNameStr.equals(user.getFirstName()) ) {
                updateFields.put("firstName", AESHelper.encrypt(firstNameStr, secretKey));
                isFirstNameChanged = true;
            }

            if ( !lastNameStr.equals(user.getLastName()) ) {
                updateFields.put("lastName", AESHelper.encrypt(lastNameStr,secretKey));
                isLastNameChanged = true;
            }

            if ( !newPassStr.isEmpty() ) {
                if (newPassConfStr.equals(newPassStr)) {
                    updateFields.put("password", AESHelper.encrypt(newPassStr, secretKey));
                    isLogoutNeeded = true;
                } else {
                    Toast.makeText(getActivity(), "New passwords don't match", Toast.LENGTH_SHORT).show();
                }
            }

            if ( !updateFields.isEmpty() ) {
                db.collection("Users").document(user.getEmail()).update(updateFields)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), " Account Details updated!", Toast.LENGTH_SHORT).show();
                                password.setText("");
                                newPassword.setText("");
                                newPasswordConfirmed.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                if ( isFirstNameChanged || isLastNameChanged ) {
                    String updatedName = firstNameStr + " " + lastNameStr;

                    // Update the user logged in details
                    NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);

                    TextView loggedInUser = headerView.findViewById(R.id.logged_in_user);
                    loggedInUser.setText(updatedName);
                }

                if ( isLogoutNeeded ) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            } else if ( newPassStr.isEmpty() ) {
                Toast.makeText(getActivity(), "No Changes Detected!", Toast.LENGTH_SHORT).show();
                password.setText("");
            }

        } else {
            Toast.makeText(getActivity(), "Incorrect Password!", Toast.LENGTH_SHORT).show();
            password.setText("");
        }
    }
}

