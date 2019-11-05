package com.mpascal.programmaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText username;
    private EditText password;
    private ProgressBar loginProgressBar;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginProgressBar = findViewById(R.id.loginProgress);
        loginProgressBar.setVisibility(View.GONE);
        username = findViewById(R.id.l_email);
        password = findViewById(R.id.l_password);

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLogin(v);
            }
        });

        Button registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });
    }

    public void validateLogin(View v) {

        String usernameStr = username.getText().toString();
        final String passwordStr = password.getText().toString();

        // show that something is done after the login button is pressed
        loginProgressBar.setVisibility(View.VISIBLE);

        db.collection("Users").document(usernameStr).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() &&
                                (documentSnapshot.getString("password").equals(passwordStr))) {
                            Intent intent = new Intent(LoginActivity.this, SurveyActivity.class);
                            intent.putExtra("firstName", documentSnapshot.getString("firstName"));
                            intent.putExtra("email", documentSnapshot.getString("email"));
                            startActivity(intent);

                            // make the progress bar invisible
                            loginProgressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();

                            // make the progress bar invisible
                            loginProgressBar.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());

                        // make the progress bar invisible
                        loginProgressBar.setVisibility(View.GONE);
                    }
                });

    }

    public void register(View v) {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
