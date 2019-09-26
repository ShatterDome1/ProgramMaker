package com.example.programmaker;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

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
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(LoginActivity.this);
            a_builder.setMessage("Incorrect credentials!");
            System.out.println("Incorrect credential!!!");
        }
    }

    public void register() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
