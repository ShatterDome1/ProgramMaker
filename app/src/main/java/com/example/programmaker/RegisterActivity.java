package com.example.programmaker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confPass;
    private Button dateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dateOfBirth = findViewById(R.id.r_dateofbirth);

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
        firstName = findViewById(R.id.r_firstname);
        lastName = findViewById(R.id.r_lastname);
        email = findViewById(R.id.r_email);
        password = findViewById(R.id.r_password);
        confPass = findViewById(R.id.r_confpassword);
        dateOfBirth = findViewById(R.id.r_dateofbirth);

        String firstNameStr = firstName.getText().toString();
        String lastNameStr = lastName.getText().toString();
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();
        String confPassStr = confPass.getText().toString();
        String dateOfBirthStr = dateOfBirth.getText().toString();

        if ( !firstNameStr.isEmpty() &&
             !lastNameStr.isEmpty() &&
             !emailStr.isEmpty() &&
             !passwordStr.isEmpty() &&
             !dateOfBirthStr.isEmpty() &&
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
