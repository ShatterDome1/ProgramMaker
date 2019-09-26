package com.example.programmaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.programmager.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the spinner
        Spinner q1spinner = (Spinner) findViewById(R.id.q1_spinner);
        // The adapter gets the string list from the resources
        ArrayAdapter<String> q1adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.q1_answers));
        // This sets the view of the spinner
        q1adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Add the values from the string list to the spinner
        q1spinner.setAdapter(q1adapter);
    }

    // Called when the user taps the Send button
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
