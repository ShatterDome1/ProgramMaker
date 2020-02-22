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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mpascal.programmaker.db.User;
import com.mpascal.programmaker.util.AESHelper;

import java.util.Calendar;

import javax.crypto.SecretKey;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "RegisterActivity";

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confPass;
    private Button dateOfBirth;
    private ProgressBar progressBar;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        if ( !firstNameStr.isEmpty() &&
             !lastNameStr.isEmpty() &&
             !emailStr.isEmpty() &&
             !passwordStr.isEmpty() &&
             !dateOfBirthStr.isEmpty() &&
             passwordStr.equals(confPassStr) ) {

            SecretKey secretKey = AESHelper.generateKey();
            String secretKeyStr = AESHelper.convertSecretKeyToString(secretKey);

            final User newUser = new User(AESHelper.encrypt(firstNameStr, secretKey),
                                          AESHelper.encrypt(lastNameStr, secretKey),
                                          AESHelper.encrypt(emailStr, secretKey),
                                          AESHelper.encrypt(passwordStr, secretKey),
                                          AESHelper.encrypt(dateOfBirthStr, secretKey),
                                          secretKeyStr);

            final DocumentReference accountRef = db.collection("Users").document(emailStr);

            // Check if user already exists
            accountRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Toast.makeText(RegisterActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();

                                // make the progress bar invisible
                                progressBar.setVisibility(View.GONE);
                            } else {
                                // Add user to database
                                accountRef.set(newUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RegisterActivity.this, "User Registered!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, e.toString());

                                                // make the progress bar invisible
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());

                            // make the progress bar invisible
                            progressBar.setVisibility(View.GONE);
                        }
                    });

        } else {
            Toast.makeText(this, "Fields incorrectly populated!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

    }
}
