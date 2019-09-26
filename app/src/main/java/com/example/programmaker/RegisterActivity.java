package com.example.programmaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = findViewById(R.id.register_action);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void register() {
        EditText firstName = findViewById(R.id.r_firstname);
        EditText lastName = findViewById(R.id.r_lastname);
        EditText email = findViewById(R.id.r_email);
        EditText password = findViewById(R.id.r_password);
        EditText confPass = findViewById(R.id.r_confpassword);

        String firstNameStr = firstName.getText().toString();
        String lastNameStr = lastName.getText().toString();
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();
        String confPassStr = confPass.getText().toString();

        if ( !firstNameStr.isEmpty() &&
             !lastNameStr.isEmpty() &&
             !emailStr.isEmpty() &&
             !passwordStr.isEmpty() &&
             passwordStr.equals(confPassStr) ) {

            User user = new User(emailStr, passwordStr, firstNameStr, lastNameStr);
            UserDBHandler dbHandler = new UserDBHandler(this,null);
            boolean ok = dbHandler.addHandler(user);
            if (ok) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}
