package com.mpascal.programmaker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mpascal.programmaker.R;
import com.mpascal.programmaker.db.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private EditText firstName;
    private EditText lastName;
    private EditText password;
    private EditText newPassword;
    private EditText newPasswordConfirmed;
    private Button   updateDetailsButton;
    private TextView loggedInEmail;
    private DocumentReference accountRef;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_profile, container, false);

        firstName = view.findViewById(R.id.profileFirstName);
        lastName = view.findViewById(R.id.profileLastName);
        password = view.findViewById(R.id.profilePassword);
        newPassword = view.findViewById(R.id.profileNewPassword);
        newPasswordConfirmed = view.findViewById(R.id.profileNewPasswordConfirm);
        updateDetailsButton = view.findViewById(R.id.updateDetails);


        // Get the logged in email from the navigation tab header
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        loggedInEmail = headerView.findViewById(R.id.logged_in_email);

        // Set the document ref to be the user's account
        accountRef = db.collection("Users").document(loggedInEmail.getText().toString());

        accountRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            firstName.setText(user.getFirstName());
                            lastName.setText(user.getLastName());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity() , "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetails();
            }
        });

        return view;
    }

    private void updateDetails() {

        final String passwordStr = password.getText().toString();
        final String newPassStr = newPassword.getText().toString();
        final String newPassConfStr = newPasswordConfirmed.getText().toString();
        final String firstNameStr = firstName.getText().toString();
        final String lastNameStr = lastName.getText().toString();

        accountRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> updateFields = new HashMap<>();

                        if(documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (passwordStr.equals(user.getPassword())) {

                                if (!firstNameStr.equals(user.getFirstName())) {
                                    updateFields.put("firstName", firstNameStr);
                                }

                                if (!lastNameStr.equals(user.getLastName())) {
                                    updateFields.put("lastName", lastNameStr);
                                }

                                if (!newPassStr.isEmpty() && newPassConfStr.equals(newPassStr)) {
                                    updateFields.put("password", newPassStr);
                                }

                                if (!updateFields.isEmpty()) {
                                    accountRef.update(updateFields)
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
                                } else {
                                    Toast.makeText(getActivity(), "No Changes Detected!", Toast.LENGTH_SHORT).show();
                                    password.setText("");
                                }

                            } else {
                                Toast.makeText(getActivity(), "Incorrect Password!", Toast.LENGTH_SHORT).show();
                                password.setText("");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity() , "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

}
