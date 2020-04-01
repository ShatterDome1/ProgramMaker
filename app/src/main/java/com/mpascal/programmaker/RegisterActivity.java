package com.mpascal.programmaker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mpascal.programmaker.db.UserDB;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "RegisterActivity";
    private static final String PACKAGE_NAME = "com.mpascal.programmaker";

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confPass;
    private Button dateOfBirth;
    private ProgressBar progressBar;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Activity user entered
        firstName = findViewById(R.id.r_firstname);
        lastName = findViewById(R.id.r_lastname);
        email = findViewById(R.id.r_email);
        password = findViewById(R.id.r_password);
        confPass = findViewById(R.id.r_confpassword);
        dateOfBirth = findViewById(R.id.r_dateofbirth);

        progressBar = findViewById(R.id.registerProgress);
        progressBar.setVisibility(View.GONE);

        // Get the date of birth using a date picker dialog box
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Button registerButton = findViewById(R.id.register_action);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegisterActivity.this,
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
        dateOfBirth.setText(date);
    }

    private void register() {
        String firstNameStr = firstName.getText().toString();
        String lastNameStr = lastName.getText().toString();
        final String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();
        String confPassStr = confPass.getText().toString();
        String dateOfBirthStr = dateOfBirth.getText().toString();

        // show that something is happening using the progress bar
        progressBar.setVisibility(View.VISIBLE);

        if (!firstNameStr.isEmpty() &&
                !lastNameStr.isEmpty() &&
                !emailStr.isEmpty() &&
                !passwordStr.isEmpty() &&
                !dateOfBirthStr.isEmpty() &&
                passwordStr.equals(confPassStr)) {

            final UserDB newUser = new UserDB(firstNameStr, lastNameStr, emailStr, dateOfBirthStr);

            // Check if user already exists
            auth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        final FirebaseUser user = auth.getCurrentUser();

                        // Add user to firestore
                        db.collection("Users").document(emailStr).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Verification email sent to " + user.getEmail() + "!", Toast.LENGTH_SHORT).show();
                                            auth.signOut();

                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to send verification email!", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onComplete: failed", task.getException());

                                            // make the progress bar invisible
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "Error while registering!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());

                                // make the progress bar invisible
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());

                        if (task.getException().toString().contains("The email address is already in use by another account")) {
                            Toast.makeText(RegisterActivity.this, "Email already in use!", Toast.LENGTH_SHORT).show();
                        } else if (task.getException().toString().contains("Password should be at least 6 characters")) {
                            Toast.makeText(RegisterActivity.this, "Password should be at least 6 characters!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }

                        // make the progress bar invisible
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

        } else {
            Toast.makeText(this, "Fields incorrectly populated!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

    }
}
