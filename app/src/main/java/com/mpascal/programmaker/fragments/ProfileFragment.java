package com.mpascal.programmaker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mpascal.programmaker.LoginActivity;
import com.mpascal.programmaker.MainActivity;
import com.mpascal.programmaker.R;
import com.mpascal.programmaker.db.UserDB;
import com.mpascal.programmaker.dialogs.ChangeDateOfBirthDialog;
import com.mpascal.programmaker.dialogs.ChangeFirstNameDialog;
import com.mpascal.programmaker.dialogs.ChangeLastNameDialog;
import com.mpascal.programmaker.dialogs.ChangePasswordDialog;
import com.mpascal.programmaker.dialogs.DeleteAccountDialog;
import com.mpascal.programmaker.repositories.RoutineRepository;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private TextView firstName;
    private TextView lastName;
    private TextView email;
    private TextView dateOfBirth;

    private ImageView editFirstName;
    private ImageView editLastName;
    private ImageView editEmail;
    private ImageView editDateOfBirth;

    private Button changePasswordButton;
    private Button deleteAccount;

    private UserDB user;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get the Bundle given by the Main Activity
        Bundle bundle = getArguments();
        user = bundle.getParcelable(MainActivity.PACKAGE_NAME + ".userDetails");

        firstName = view.findViewById(R.id.profileFirstName);
        firstName.setText(user.getFirstName());

        lastName = view.findViewById(R.id.profileLastName);
        lastName.setText(user.getLastName());

        email = view.findViewById(R.id.profileEmail);
        email.setText(user.getEmail());

        dateOfBirth = view.findViewById(R.id.profileDateOfBirth);
        dateOfBirth.setText(user.getDateOfBirth());

        editFirstName = view.findViewById(R.id.edit_first_name);
        editFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFirstNameDialog changeFirstNameDialog = new ChangeFirstNameDialog();
                changeFirstNameDialog.show(getFragmentManager(), "ChangeFirstName");
            }
        });

        editLastName = view.findViewById(R.id.edit_last_name);
        editLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeLastNameDialog changeLastNameDialog = new ChangeLastNameDialog();
                changeLastNameDialog.show(getFragmentManager(), "ChangeLastName");
            }
        });

        editDateOfBirth = view.findViewById(R.id.edit_date_of_birth);
        editDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeDateOfBirthDialog changeDateOfBirthDialog = new ChangeDateOfBirthDialog();
                changeDateOfBirthDialog.show(getFragmentManager(), "ChangeDateOfBirth");
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

    public void changeFirstName(final String newFirstName) {
        if (!newFirstName.equals(user.getFirstName()) && !newFirstName.isEmpty()) {
            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put("firstName", newFirstName);

            db.collection("Users").document(user.getEmail()).update(updateFields)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "First name updated", Toast.LENGTH_SHORT).show();
                            String updatedName = newFirstName + " " + user.getLastName();

                            firstName.setText(newFirstName);
                            user.setFirstName(newFirstName);

                            // Update the user logged in details
                            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                            View headerView = navigationView.getHeaderView(0);

                            TextView loggedInUser = headerView.findViewById(R.id.logged_in_user);
                            loggedInUser.setText(updatedName);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.toString());
                    Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No changes detected", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeLastName(final String newLastName) {
        if (!newLastName.equals(user.getLastName()) && !newLastName.isEmpty()) {
            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put("lastName", newLastName);

            db.collection("Users").document(user.getEmail()).update(updateFields).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getActivity(), "Last name updated", Toast.LENGTH_SHORT).show();
                    String updatedName = user.getFirstName() + " " + newLastName;

                    lastName.setText(newLastName);
                    user.setLastName(newLastName);

                    // Update the user logged in details
                    NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);

                    TextView loggedInUser = headerView.findViewById(R.id.logged_in_user);
                    loggedInUser.setText(updatedName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.toString());
                    Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No changes detected", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeDateOfBirth(final String newDateOfBirth) {
        if (!newDateOfBirth.equals(user.getDateOfBirth()) && newDateOfBirth.contains("/")) {
            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put("dateOfBirth", newDateOfBirth);

            db.collection("Users").document(user.getEmail()).update(updateFields).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getActivity(), "Date of birth updated", Toast.LENGTH_SHORT).show();
                    dateOfBirth.setText(newDateOfBirth);
                    user.setDateOfBirth(newDateOfBirth);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Date of birth update failed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            });
        } else {
            Toast.makeText(getActivity(), "No changes detected", Toast.LENGTH_SHORT).show();
        }
    }

    public void changePassword(String password, final String newPassStr, String newPassConfStr) {
        if (newPassConfStr.equals(newPassStr)) {
            final FirebaseUser currentUser = auth.getCurrentUser();

            // Get auth credentials from the user for re-authentication. The example below shows
            // email and password credentials but there are multiple possible providers,
            // such as GoogleAuthProvider or FacebookAuthProvider.
            AuthCredential credential = EmailAuthProvider
                    .getCredential(currentUser.getEmail(), password);

            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        currentUser.updatePassword(newPassStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Password Changed", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onComplete: password updated");
                                } else {
                                    Toast.makeText(getActivity(), "Password Update Failed", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onComplete: ", task.getException());
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Authentication Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "New Passwords Don't Match", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAccount(String password) {
        final FirebaseUser currentUser = auth.getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(currentUser.getEmail(), password);

        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    db.collection("Routines").whereEqualTo("email", currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // Delete Routines
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                db.collection("Routines").document(documentSnapshot.getId()).delete().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: failed" + e.toString());
                                    }
                                });
                            }

                            // Delete user record from database
                            db.collection("Users").document(currentUser.getEmail()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    // Delete Authentication user
                                    currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                auth.signOut();
                                                Log.d(TAG, "onComplete: Account Deleted");

                                                // Log out clear the data of the user
                                                RoutineRepository routineRepository = RoutineRepository.getInstance();
                                                routineRepository.clearData();

                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            } else {
                                                Log.d(TAG, "onComplete: failed", task.getException());
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: failed" + e.toString());
                                }
                            });
                        }
                    });
                } else {
                    Log.d(TAG, "onComplete: reauth failed", task.getException());
                    Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
