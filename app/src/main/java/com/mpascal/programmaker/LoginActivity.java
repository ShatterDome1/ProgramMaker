package com.mpascal.programmaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mpascal.programmaker.db.UserDB;
import com.mpascal.programmaker.dialogs.ForgotPasswordDialog;
import com.mpascal.programmaker.dialogs.LoadingDialog;

public class LoginActivity extends AppCompatActivity implements ForgotPasswordDialog.ForgotPasswordDialogListener {
    private static final String TAG = "LoginActivity";
    public static final String PACKAGE_NAME = "com.mpascal.programmaker";

    private EditText username;
    private EditText password;
    private ProgressBar loginProgressBar;
    private Button loginButton;
    private Button registerButton;
    private TextView forgotPassword;

    private LoadingDialog loadingDialog = new LoadingDialog(this);

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginProgressBar = findViewById(R.id.loginProgress);
        loginProgressBar.setVisibility(View.GONE);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                loginProgressBar.setVisibility(View.VISIBLE);
                loadingDialog.showLoadingDialog();
                getLoggedInUserDetails(currentUser.getEmail());
            } else {
                Toast.makeText(this, "Email is not verified!", Toast.LENGTH_SHORT).show();
                auth.signOut();
            }
        }

        username = findViewById(R.id.l_email);
        password = findViewById(R.id.l_password);

        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLogin();
            }
        });

        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        forgotPassword = findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordDialog forgotPasswordDialog = new ForgotPasswordDialog();
                forgotPasswordDialog.show(getSupportFragmentManager(), "ForgotPasswordDialog");
            }
        });
    }

    private void validateLogin() {

        final String usernameStr = username.getText().toString();
        final String passwordStr = password.getText().toString();

        if (!usernameStr.isEmpty() && !passwordStr.isEmpty()) {
            // show that something is done after the login button is pressed
            loginProgressBar.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(usernameStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();

                        if (user.isEmailVerified()) {
                            getLoggedInUserDetails(usernameStr);
                        } else {
                            Toast.makeText(LoginActivity.this, "Email is not verified", Toast.LENGTH_SHORT).show();

                            // show that something is done after the login button is pressed
                            auth.signOut();
                            loginProgressBar.setVisibility(View.GONE);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed",
                                Toast.LENGTH_SHORT).show();

                        loginProgressBar.setVisibility(View.GONE);
                    }
                }
            });

        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void register() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void getLoggedInUserDetails(final String email){
        // Retrieve user from database and go to mainpage if user is logged in
        db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    UserDB user = documentSnapshot.toObject(UserDB.class);
                    user.setEmail(email);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(PACKAGE_NAME + ".userDetails", user);

                    startActivity(intent);
                }

                // make the progress bar invisible
                loadingDialog.dismissLoadingDialog();
                loginProgressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());

                // make the progress bar invisible
                loginProgressBar.setVisibility(View.GONE);
                loadingDialog.dismissLoadingDialog();
            }
        });
    }

    @Override
    public void sendForgotPasswordEmail(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Reset password email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onComplete: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Reset password email not sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
