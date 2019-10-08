package com.example.programmaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLogin();
            }
        });

        // Get a reference to the ViewModel for this screen.
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        Button registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void validateLogin() {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();

        UserDBHandler dbHandler = new UserDBHandler(this,null);
        User user = dbHandler.findHandler(usernameStr);
        if ( user != null && user.getPassword().equals(passwordStr) ) {
            Intent intent = new Intent(LoginActivity.this, SurveyActivity.class);
            startActivity(intent);
        } else {
            // Error handling when user is invalid
            String displayText = "Invalid credentials!";
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(this, displayText, duration).show();
        }
    }

    public void register() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
