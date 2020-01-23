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
import com.mpascal.programmaker.db.User;

import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText username;
    private EditText password;
    private ProgressBar loginProgressBar;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                validateLogin();
            }
        });

        Button registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void validateLogin() {

        final String usernameStr = username.getText().toString();

        if (! usernameStr.isEmpty()) {

            final String passwordStr = password.getText().toString();

            // show that something is done after the login button is pressed
            loginProgressBar.setVisibility(View.VISIBLE);

            db.collection("Users").document(usernameStr).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            boolean ok = false;
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);

                                // Retrieve encoded key and convert to SecretKey
                                SecretKey secretKey = AESHelper.convertStringToSecretKey(user.getKey());

                                // Decrypt user password
                                String passwordDec = AESHelper.decrypt(user.getPassword(), secretKey);

                                if (passwordStr.equals(passwordDec)) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                    // Retrieve decrypted firstName and email
                                    String firstNameDec = AESHelper.decrypt(user.getFirstName(), secretKey);
                                    String lastNameDec = AESHelper.decrypt(user.getLastName(), secretKey);
                                    String emailDec = AESHelper.decrypt(user.getEmail(), secretKey);

                                    intent.putExtra("fullName", firstNameDec + " " + lastNameDec);
                                    intent.putExtra("email", emailDec);
                                    startActivity(intent);
                                    ok = true;
                                }
                            }

                            if (!ok) {
                                Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                            }

                            // make the progress bar invisible
                            loginProgressBar.setVisibility(View.GONE);
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
        } else {
            Toast.makeText(this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
        }
    }

    private void register() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
