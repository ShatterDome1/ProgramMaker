package com.mpascal.programmaker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.db.UserDB;
import com.mpascal.programmaker.dialogs.LoadingDialog;
import com.mpascal.programmaker.fragments.RoutineFragment;
import com.mpascal.programmaker.viewmodels.RoutineFragmentViewModel;
import com.mpascal.programmaker.viewmodels.RoutineViewActivityViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

public class RoutineViewActivity extends AppCompatActivity {

    // Request code for selecting a PDF document.
    private static final int CREATE_FILE = 1;

    private EditText routineTitle;
    private Button updateRoutine;
    private Routine currentRoutine;
    private UserDB currentUser;
    // This is needed to update the value from the repository
    // without having to do a delete and then add on the ArrayList
    private int routinePosition;

    // 3 Blocks 7 exercise rows
    private TableLayout exerciseTableLayout;
    private TableRow[][] exerciseTableRows = new TableRow[3][7];
    // 1st [] -> Block
    // 2nd [] -> Day
    // 3rd [] -> Exercise
    private TextView[][][] exerciseCells;

    private TableLayout intensityTableLayout;
    // 1st [] -> exerciseType
    // 2nd [] -> block
    // 3rd [] -> week
    private TextView[][][] intensityCells;

    // Needed for creating the pdf file
    private ConstraintLayout constraintLayout;

    private ViewModelProvider viewModelProvider;
    private RoutineViewActivityViewModel routineViewActivityViewModel;

    private LoadingDialog loadingDialog;

    private static final String TAG = "RoutineViewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_view);

        // Get the routine clicked
        Intent intent = getIntent();
        currentRoutine = intent.getParcelableExtra(RoutineFragment.PACKAGE_NAME + ".routine");
        currentUser = intent.getParcelableExtra(RoutineFragment.PACKAGE_NAME + ".user");
        // The default value should never happen
        routinePosition = intent.getIntExtra(RoutineFragment.PACKAGE_NAME + ".routinePosition", -1);

        routineTitle = findViewById(R.id.routine_view_title);
        routineTitle.setText(currentRoutine.getTitle());

        exerciseTableLayout = findViewById(R.id.table_exercises);
        initialiseExerciseTableRows();
        initialiseExerciseCells();
        loadExercises();

        intensityTableLayout = findViewById(R.id.table_intensity);
        initialiseIntensityCells();
        loadIntensity();

        constraintLayout = findViewById(R.id.routine_view_layout);

        viewModelProvider = new ViewModelProvider(this);
        routineViewActivityViewModel = viewModelProvider.get(RoutineViewActivityViewModel.class);

        routineViewActivityViewModel.init(currentUser.getEmail());

        routineViewActivityViewModel.getIsFetchingData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, "onChanged: Fetching routines from repository");
                    loadingDialog.showLoadingDialog();
                } else {
                    loadingDialog.dismissLoadingDialog();
                }
            }
        });

        loadingDialog = new LoadingDialog(this);
        routineViewActivityViewModel.getIsUpdatingRoutine().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case 0:
                        loadingDialog.showLoadingDialog();
                        Log.d(TAG, "onChanged: Update in progress");
                        break;

                    case 1:
                        loadingDialog.dismissLoadingDialog();
                        Toast.makeText(RoutineViewActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        loadingDialog.dismissLoadingDialog();
                        Toast.makeText(RoutineViewActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onChanged: Update failed in deletion step");
                        break;

                    case 3:
                        loadingDialog.dismissLoadingDialog();
                        Toast.makeText(RoutineViewActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onChanged: Update failed in replace step");
                        break;

                    case 4:
                        loadingDialog.dismissLoadingDialog();
                        Toast.makeText(RoutineViewActivity.this, "Title already in use", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });

        updateRoutine = findViewById(R.id.routine_view_update_routine);
        updateRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails();
            }
        });
    }

    private void updateDetails() {
        String title = routineTitle.getText().toString();
        if (!currentRoutine.getTitle().equals(routineTitle.getText().toString())) {
            routineViewActivityViewModel.updateRoutine(currentRoutine, title, routinePosition);
        } else {
            Toast.makeText(this, "No changes detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routine_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.download_pdf) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PackageManager.PERMISSION_GRANTED);

            // Choose a directory using the system's file picker.
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, currentRoutine.getTitle() + ".pdf");

            startActivityForResult(intent, CREATE_FILE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri;
            if (data != null) {
                uri = data.getData();

                // Hide the update routine button before drawing the constraint layout
                updateRoutine.setVisibility(View.GONE);

                // Converts the layout and it's contents to a bitmap
                PdfDocument document = new PdfDocument();
                PdfDocument.PageInfo firstPage =  new PdfDocument.PageInfo.Builder(constraintLayout.getWidth(), constraintLayout.getHeight(), 1).create();

                PdfDocument.Page page = document.startPage(firstPage);
                constraintLayout.draw(page.getCanvas());
                document.finishPage(page);

                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);

                    document.writeTo(outputStream);

                    document.close();
                    Log.d(TAG, "onActivityResult: created");
                } catch (IOException e) {
                    Log.d(TAG, "onActivityResult: failed");
                    e.printStackTrace();
                } finally {
                    // Whatever the result of the action make sure the button is visible again
                    updateRoutine.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private int initialiseExerciseTableRows() {
        // Block1
        exerciseTableRows[0][0] = findViewById(R.id.exercise1_block1_row);
        exerciseTableRows[0][1] = findViewById(R.id.exercise2_block1_row);
        exerciseTableRows[0][2] = findViewById(R.id.exercise3_block1_row);
        exerciseTableRows[0][3] = findViewById(R.id.exercise4_block1_row);
        exerciseTableRows[0][4] = findViewById(R.id.exercise5_block1_row);
        exerciseTableRows[0][5] = findViewById(R.id.exercise6_block1_row);
        exerciseTableRows[0][6] = findViewById(R.id.exercise7_block1_row);

        // Block2
        exerciseTableRows[1][0] = findViewById(R.id.exercise1_block2_row);
        exerciseTableRows[1][1] = findViewById(R.id.exercise2_block2_row);
        exerciseTableRows[1][2] = findViewById(R.id.exercise3_block2_row);
        exerciseTableRows[1][3] = findViewById(R.id.exercise4_block2_row);
        exerciseTableRows[1][4] = findViewById(R.id.exercise5_block2_row);
        exerciseTableRows[1][5] = findViewById(R.id.exercise6_block2_row);
        exerciseTableRows[1][6] = findViewById(R.id.exercise7_block2_row);

        // Block3
        exerciseTableRows[2][0] = findViewById(R.id.exercise1_block3_row);
        exerciseTableRows[2][1] = findViewById(R.id.exercise2_block3_row);
        exerciseTableRows[2][2] = findViewById(R.id.exercise3_block3_row);
        exerciseTableRows[2][3] = findViewById(R.id.exercise4_block3_row);
        exerciseTableRows[2][4] = findViewById(R.id.exercise5_block3_row);
        exerciseTableRows[2][5] = findViewById(R.id.exercise6_block3_row);
        exerciseTableRows[2][6] = findViewById(R.id.exercise7_block3_row);


        int maxNoOfExercises = 0;
        String[][] exercisesPerBlock = currentRoutine.getExercisesPerBlock();

        // Go through every block and determine the max no of exercises
        for (int i = 0; i < 3; i++) {
            maxNoOfExercises = 0;
            for (int j = 0; j < currentRoutine.getDaysAvailable().size(); j++) {
                int noOfExercises = exercisesPerBlock[i][j].split(Pattern.quote("|")).length;

                if (noOfExercises > maxNoOfExercises) {
                    maxNoOfExercises = noOfExercises;
                }
            }

            // Set the view to gone for rows that are not needed (not holding any exercise)
            for (int k = maxNoOfExercises; k < 7; k++) {
                exerciseTableRows[i][k].setVisibility(View.GONE);
            }
        }

        return maxNoOfExercises;
    }

    private void initialiseExerciseCells() {
        // 1st [] -> Block
        // 2nd [] -> Day
        // 3rd [] -> Exercise
        exerciseCells = new TextView[3][7][7];

        // Initialise only the TextViews that we will need
        ArrayList<Integer> daysAvailable = currentRoutine.getDaysAvailable();

        for (Integer day : daysAvailable) {
            switch (day) {
                // Monday
                case 0:
                    exerciseCells[0][0][0] = findViewById(R.id.exercise1_block1_day0);
                    exerciseCells[0][0][1] = findViewById(R.id.exercise2_block1_day0);
                    exerciseCells[0][0][2] = findViewById(R.id.exercise3_block1_day0);
                    exerciseCells[0][0][3] = findViewById(R.id.exercise4_block1_day0);
                    exerciseCells[0][0][4] = findViewById(R.id.exercise5_block1_day0);
                    exerciseCells[0][0][5] = findViewById(R.id.exercise6_block1_day0);
                    exerciseCells[0][0][6] = findViewById(R.id.exercise7_block1_day0);

                    exerciseCells[1][0][0] = findViewById(R.id.exercise1_block2_day0);
                    exerciseCells[1][0][1] = findViewById(R.id.exercise2_block2_day0);
                    exerciseCells[1][0][2] = findViewById(R.id.exercise3_block2_day0);
                    exerciseCells[1][0][3] = findViewById(R.id.exercise4_block2_day0);
                    exerciseCells[1][0][4] = findViewById(R.id.exercise5_block2_day0);
                    exerciseCells[1][0][5] = findViewById(R.id.exercise6_block2_day0);
                    exerciseCells[1][0][6] = findViewById(R.id.exercise7_block2_day0);

                    exerciseCells[2][0][0] = findViewById(R.id.exercise1_block3_day0);
                    exerciseCells[2][0][1] = findViewById(R.id.exercise2_block3_day0);
                    exerciseCells[2][0][2] = findViewById(R.id.exercise3_block3_day0);
                    exerciseCells[2][0][3] = findViewById(R.id.exercise4_block3_day0);
                    exerciseCells[2][0][4] = findViewById(R.id.exercise5_block3_day0);
                    exerciseCells[2][0][5] = findViewById(R.id.exercise6_block3_day0);
                    exerciseCells[2][0][6] = findViewById(R.id.exercise7_block3_day0);
                    break;

                // Tuesday
                case 1:
                    exerciseCells[0][1][0] = findViewById(R.id.exercise1_block1_day1);
                    exerciseCells[0][1][1] = findViewById(R.id.exercise2_block1_day1);
                    exerciseCells[0][1][2] = findViewById(R.id.exercise3_block1_day1);
                    exerciseCells[0][1][3] = findViewById(R.id.exercise4_block1_day1);
                    exerciseCells[0][1][4] = findViewById(R.id.exercise5_block1_day1);
                    exerciseCells[0][1][5] = findViewById(R.id.exercise6_block1_day1);
                    exerciseCells[0][1][6] = findViewById(R.id.exercise7_block1_day1);

                    exerciseCells[1][1][0] = findViewById(R.id.exercise1_block2_day1);
                    exerciseCells[1][1][1] = findViewById(R.id.exercise2_block2_day1);
                    exerciseCells[1][1][2] = findViewById(R.id.exercise3_block2_day1);
                    exerciseCells[1][1][3] = findViewById(R.id.exercise4_block2_day1);
                    exerciseCells[1][1][4] = findViewById(R.id.exercise5_block2_day1);
                    exerciseCells[1][1][5] = findViewById(R.id.exercise6_block2_day1);
                    exerciseCells[1][1][6] = findViewById(R.id.exercise7_block2_day1);

                    exerciseCells[2][1][0] = findViewById(R.id.exercise1_block3_day1);
                    exerciseCells[2][1][1] = findViewById(R.id.exercise2_block3_day1);
                    exerciseCells[2][1][2] = findViewById(R.id.exercise3_block3_day1);
                    exerciseCells[2][1][3] = findViewById(R.id.exercise4_block3_day1);
                    exerciseCells[2][1][4] = findViewById(R.id.exercise5_block3_day1);
                    exerciseCells[2][1][5] = findViewById(R.id.exercise6_block3_day1);
                    exerciseCells[2][1][6] = findViewById(R.id.exercise7_block3_day1);
                    break;

                case 2:
                    exerciseCells[0][2][0] = findViewById(R.id.exercise1_block1_day2);
                    exerciseCells[0][2][1] = findViewById(R.id.exercise2_block1_day2);
                    exerciseCells[0][2][2] = findViewById(R.id.exercise3_block1_day2);
                    exerciseCells[0][2][3] = findViewById(R.id.exercise4_block1_day2);
                    exerciseCells[0][2][4] = findViewById(R.id.exercise5_block1_day2);
                    exerciseCells[0][2][5] = findViewById(R.id.exercise6_block1_day2);
                    exerciseCells[0][2][6] = findViewById(R.id.exercise7_block1_day2);

                    exerciseCells[1][2][0] = findViewById(R.id.exercise1_block2_day2);
                    exerciseCells[1][2][1] = findViewById(R.id.exercise2_block2_day2);
                    exerciseCells[1][2][2] = findViewById(R.id.exercise3_block2_day2);
                    exerciseCells[1][2][3] = findViewById(R.id.exercise4_block2_day2);
                    exerciseCells[1][2][4] = findViewById(R.id.exercise5_block2_day2);
                    exerciseCells[1][2][5] = findViewById(R.id.exercise6_block2_day2);
                    exerciseCells[1][2][6] = findViewById(R.id.exercise7_block2_day2);

                    exerciseCells[2][2][0] = findViewById(R.id.exercise1_block3_day2);
                    exerciseCells[2][2][1] = findViewById(R.id.exercise2_block3_day2);
                    exerciseCells[2][2][2] = findViewById(R.id.exercise3_block3_day2);
                    exerciseCells[2][2][3] = findViewById(R.id.exercise4_block3_day2);
                    exerciseCells[2][2][4] = findViewById(R.id.exercise5_block3_day2);
                    exerciseCells[2][2][5] = findViewById(R.id.exercise6_block3_day2);
                    exerciseCells[2][2][6] = findViewById(R.id.exercise7_block3_day2);
                    break;

                case 3:
                    exerciseCells[0][3][0] = findViewById(R.id.exercise1_block1_day3);
                    exerciseCells[0][3][1] = findViewById(R.id.exercise2_block1_day3);
                    exerciseCells[0][3][2] = findViewById(R.id.exercise3_block1_day3);
                    exerciseCells[0][3][3] = findViewById(R.id.exercise4_block1_day3);
                    exerciseCells[0][3][4] = findViewById(R.id.exercise5_block1_day3);
                    exerciseCells[0][3][5] = findViewById(R.id.exercise6_block1_day3);
                    exerciseCells[0][3][6] = findViewById(R.id.exercise7_block1_day3);

                    exerciseCells[1][3][0] = findViewById(R.id.exercise1_block2_day3);
                    exerciseCells[1][3][1] = findViewById(R.id.exercise2_block2_day3);
                    exerciseCells[1][3][2] = findViewById(R.id.exercise3_block2_day3);
                    exerciseCells[1][3][3] = findViewById(R.id.exercise4_block2_day3);
                    exerciseCells[1][3][4] = findViewById(R.id.exercise5_block2_day3);
                    exerciseCells[1][3][5] = findViewById(R.id.exercise6_block2_day3);
                    exerciseCells[1][3][6] = findViewById(R.id.exercise7_block2_day3);

                    exerciseCells[2][3][0] = findViewById(R.id.exercise1_block3_day3);
                    exerciseCells[2][3][1] = findViewById(R.id.exercise2_block3_day3);
                    exerciseCells[2][3][2] = findViewById(R.id.exercise3_block3_day3);
                    exerciseCells[2][3][3] = findViewById(R.id.exercise4_block3_day3);
                    exerciseCells[2][3][4] = findViewById(R.id.exercise5_block3_day3);
                    exerciseCells[2][3][5] = findViewById(R.id.exercise6_block3_day3);
                    exerciseCells[2][3][6] = findViewById(R.id.exercise7_block3_day3);
                    break;

                case 4:
                    exerciseCells[0][4][0] = findViewById(R.id.exercise1_block1_day4);
                    exerciseCells[0][4][1] = findViewById(R.id.exercise2_block1_day4);
                    exerciseCells[0][4][2] = findViewById(R.id.exercise3_block1_day4);
                    exerciseCells[0][4][3] = findViewById(R.id.exercise4_block1_day4);
                    exerciseCells[0][4][4] = findViewById(R.id.exercise5_block1_day4);
                    exerciseCells[0][4][5] = findViewById(R.id.exercise6_block1_day4);
                    exerciseCells[0][4][6] = findViewById(R.id.exercise7_block1_day4);

                    exerciseCells[1][4][0] = findViewById(R.id.exercise1_block2_day4);
                    exerciseCells[1][4][1] = findViewById(R.id.exercise2_block2_day4);
                    exerciseCells[1][4][2] = findViewById(R.id.exercise3_block2_day4);
                    exerciseCells[1][4][3] = findViewById(R.id.exercise4_block2_day4);
                    exerciseCells[1][4][4] = findViewById(R.id.exercise5_block2_day4);
                    exerciseCells[1][4][5] = findViewById(R.id.exercise6_block2_day4);
                    exerciseCells[1][4][6] = findViewById(R.id.exercise7_block2_day4);

                    exerciseCells[2][4][0] = findViewById(R.id.exercise1_block3_day4);
                    exerciseCells[2][4][1] = findViewById(R.id.exercise2_block3_day4);
                    exerciseCells[2][4][2] = findViewById(R.id.exercise3_block3_day4);
                    exerciseCells[2][4][3] = findViewById(R.id.exercise4_block3_day4);
                    exerciseCells[2][4][4] = findViewById(R.id.exercise5_block3_day4);
                    exerciseCells[2][4][5] = findViewById(R.id.exercise6_block3_day4);
                    exerciseCells[2][4][6] = findViewById(R.id.exercise7_block3_day4);
                    break;

                case 5:
                    exerciseCells[0][5][0] = findViewById(R.id.exercise1_block1_day5);
                    exerciseCells[0][5][1] = findViewById(R.id.exercise2_block1_day5);
                    exerciseCells[0][5][2] = findViewById(R.id.exercise3_block1_day5);
                    exerciseCells[0][5][3] = findViewById(R.id.exercise4_block1_day5);
                    exerciseCells[0][5][4] = findViewById(R.id.exercise5_block1_day5);
                    exerciseCells[0][5][5] = findViewById(R.id.exercise6_block1_day5);
                    exerciseCells[0][5][6] = findViewById(R.id.exercise7_block1_day5);

                    exerciseCells[1][5][0] = findViewById(R.id.exercise1_block2_day5);
                    exerciseCells[1][5][1] = findViewById(R.id.exercise2_block2_day5);
                    exerciseCells[1][5][2] = findViewById(R.id.exercise3_block2_day5);
                    exerciseCells[1][5][3] = findViewById(R.id.exercise4_block2_day5);
                    exerciseCells[1][5][4] = findViewById(R.id.exercise5_block2_day5);
                    exerciseCells[1][5][5] = findViewById(R.id.exercise6_block2_day5);
                    exerciseCells[1][5][6] = findViewById(R.id.exercise7_block2_day5);

                    exerciseCells[2][5][0] = findViewById(R.id.exercise1_block3_day5);
                    exerciseCells[2][5][1] = findViewById(R.id.exercise2_block3_day5);
                    exerciseCells[2][5][2] = findViewById(R.id.exercise3_block3_day5);
                    exerciseCells[2][5][3] = findViewById(R.id.exercise4_block3_day5);
                    exerciseCells[2][5][4] = findViewById(R.id.exercise5_block3_day5);
                    exerciseCells[2][5][5] = findViewById(R.id.exercise6_block3_day5);
                    exerciseCells[2][5][6] = findViewById(R.id.exercise7_block3_day5);
                    break;

                case 6:
                    exerciseCells[0][6][0] = findViewById(R.id.exercise1_block1_day6);
                    exerciseCells[0][6][1] = findViewById(R.id.exercise2_block1_day6);
                    exerciseCells[0][6][2] = findViewById(R.id.exercise3_block1_day6);
                    exerciseCells[0][6][3] = findViewById(R.id.exercise4_block1_day6);
                    exerciseCells[0][6][4] = findViewById(R.id.exercise5_block1_day6);
                    exerciseCells[0][6][5] = findViewById(R.id.exercise6_block1_day6);
                    exerciseCells[0][6][6] = findViewById(R.id.exercise7_block1_day6);

                    exerciseCells[1][6][0] = findViewById(R.id.exercise1_block2_day6);
                    exerciseCells[1][6][1] = findViewById(R.id.exercise2_block2_day6);
                    exerciseCells[1][6][2] = findViewById(R.id.exercise3_block2_day6);
                    exerciseCells[1][6][3] = findViewById(R.id.exercise4_block2_day6);
                    exerciseCells[1][6][4] = findViewById(R.id.exercise5_block2_day6);
                    exerciseCells[1][6][5] = findViewById(R.id.exercise6_block2_day6);
                    exerciseCells[1][6][6] = findViewById(R.id.exercise7_block2_day6);

                    exerciseCells[2][6][0] = findViewById(R.id.exercise1_block3_day6);
                    exerciseCells[2][6][1] = findViewById(R.id.exercise2_block3_day6);
                    exerciseCells[2][6][2] = findViewById(R.id.exercise3_block3_day6);
                    exerciseCells[2][6][3] = findViewById(R.id.exercise4_block3_day6);
                    exerciseCells[2][6][4] = findViewById(R.id.exercise5_block3_day6);
                    exerciseCells[2][6][5] = findViewById(R.id.exercise6_block3_day6);
                    exerciseCells[2][6][6] = findViewById(R.id.exercise7_block3_day6);
                    break;
            }
        }

    }

    private void initialiseIntensityCells() {
        // 1st [] -> exerciseType
        // 2nd [] -> block
        // 3rd [] -> week
        intensityCells = new TextView[4][3][4];

        // Block 1
        intensityCells[0][0][0] = findViewById(R.id.main_block1_week1);
        intensityCells[1][0][0] = findViewById(R.id.secondary_block1_week1);
        intensityCells[2][0][0] = findViewById(R.id.accessory_block1_week1);
        intensityCells[3][0][0] = findViewById(R.id.cardio_block1_week1);

        intensityCells[0][0][1] = findViewById(R.id.main_block1_week2);
        intensityCells[1][0][1] = findViewById(R.id.secondary_block1_week2);
        intensityCells[2][0][1] = findViewById(R.id.accessory_block1_week2);
        intensityCells[3][0][1] = findViewById(R.id.cardio_block1_week2);

        intensityCells[0][0][2] = findViewById(R.id.main_block1_week3);
        intensityCells[1][0][2] = findViewById(R.id.secondary_block1_week3);
        intensityCells[2][0][2] = findViewById(R.id.accessory_block1_week3);
        intensityCells[3][0][2] = findViewById(R.id.cardio_block1_week3);

        intensityCells[0][0][3] = findViewById(R.id.main_block1_week4);
        intensityCells[1][0][3] = findViewById(R.id.secondary_block1_week4);
        intensityCells[2][0][3] = findViewById(R.id.accessory_block1_week4);
        intensityCells[3][0][3] = findViewById(R.id.cardio_block1_week4);

        // Block 2
        intensityCells[0][1][0] = findViewById(R.id.main_block2_week1);
        intensityCells[1][1][0] = findViewById(R.id.secondary_block2_week1);
        intensityCells[2][1][0] = findViewById(R.id.accessory_block2_week1);
        intensityCells[3][1][0] = findViewById(R.id.cardio_block2_week1);

        intensityCells[0][1][1] = findViewById(R.id.main_block2_week2);
        intensityCells[1][1][1] = findViewById(R.id.secondary_block2_week2);
        intensityCells[2][1][1] = findViewById(R.id.accessory_block2_week2);
        intensityCells[3][1][1] = findViewById(R.id.cardio_block2_week2);

        intensityCells[0][1][2] = findViewById(R.id.main_block2_week3);
        intensityCells[1][1][2] = findViewById(R.id.secondary_block2_week3);
        intensityCells[2][1][2] = findViewById(R.id.accessory_block2_week3);
        intensityCells[3][1][2] = findViewById(R.id.cardio_block2_week3);

        intensityCells[0][1][3] = findViewById(R.id.main_block2_week4);
        intensityCells[1][1][3] = findViewById(R.id.secondary_block2_week4);
        intensityCells[2][1][3] = findViewById(R.id.accessory_block2_week4);
        intensityCells[3][1][3] = findViewById(R.id.cardio_block2_week4);

        // Block 3
        intensityCells[0][2][0] = findViewById(R.id.main_block3_week1);
        intensityCells[1][2][0] = findViewById(R.id.secondary_block3_week1);
        intensityCells[2][2][0] = findViewById(R.id.accessory_block3_week1);
        intensityCells[3][2][0] = findViewById(R.id.cardio_block3_week1);

        intensityCells[0][2][1] = findViewById(R.id.main_block3_week2);
        intensityCells[1][2][1] = findViewById(R.id.secondary_block3_week2);
        intensityCells[2][2][1] = findViewById(R.id.accessory_block3_week2);
        intensityCells[3][2][1] = findViewById(R.id.cardio_block3_week2);

        intensityCells[0][2][2] = findViewById(R.id.main_block3_week3);
        intensityCells[1][2][2] = findViewById(R.id.secondary_block3_week3);
        intensityCells[2][2][2] = findViewById(R.id.accessory_block3_week3);
        intensityCells[3][2][2] = findViewById(R.id.cardio_block3_week3);

        intensityCells[0][2][3] = findViewById(R.id.main_block3_week4);
        intensityCells[1][2][3] = findViewById(R.id.secondary_block3_week4);
        intensityCells[2][2][3] = findViewById(R.id.accessory_block3_week4);
        intensityCells[3][2][3] = findViewById(R.id.cardio_block3_week4);
    }

    private void loadExercises() {
        String[][] exercisesPerBlock = currentRoutine.getExercisesPerBlock();
        ArrayList<Integer> daysAvailable = currentRoutine.getDaysAvailable();
        Collections.sort(daysAvailable);

        for (int i = 0; i < 3; i++) {
            int dayTemplate = 0;
            for (Integer day : daysAvailable) {
                String[] exercises = exercisesPerBlock[i][dayTemplate].split(Pattern.quote("|"));

                for (int j = 0; j < exercises.length; j++) {
                    String[] exercise = exercises[j].split("-");
                    exerciseCells[i][day][j].setText(exercise[1]);

                    if (exercise[0].equals("Main")) {
                        exerciseCells[i][day][j].setTextColor(Color.parseColor("#ffcc0000"));
                    }

                    if (exercise[0].equals("Secondary")) {
                        exerciseCells[i][day][j].setTextColor(Color.parseColor("#ff0099cc"));
                    }

                    if (exercise[0].equals("Accessory")) {
                        exerciseCells[i][day][j].setTextColor(Color.parseColor("#ff669900"));
                    }

                    if (exercise[0].equals("Cardio")) {
                        exerciseCells[i][day][j].setTextColor(Color.BLACK);
                    }
                }

                ++dayTemplate;
            }
        }

        // Collapse the columns with the days that don't have exercises
        for (int i = 0; i < 7; i++) {
            if (!daysAvailable.contains(i)) {
                exerciseTableLayout.setColumnCollapsed(i + 1, true);
            }
        }
    }

    private void loadIntensity() {
        String[][] exercisePerBlock = currentRoutine.getExercisesPerBlock();

        boolean hasMainExercises = false;
        boolean hasSecondaryExercises = false;
        boolean hasAccessoryExercises = false;
        boolean hasCardioExercises = false;

        // Find which exercise types are in the intensity provided
        for (int i = 0; i < 3; i++) {
            for(String dayTemplate : exercisePerBlock[i]) {
                String[] exercises = dayTemplate.split(Pattern.quote("|"));

                for (String exercise : exercises) {
                    String exerciseType = exercise.split("-")[0];

                    if (exerciseType.equals("Main")) {
                        hasMainExercises = true;
                    }

                    if (exerciseType.equals("Secondary")) {
                        hasSecondaryExercises = true;
                    }

                    if (exerciseType.equals("Accessory")) {
                        hasAccessoryExercises = true;
                    }

                    if (exerciseType.equals("Cardio")) {
                        hasCardioExercises = true;
                    }
                }
            }
        }

        // Collapse columns that are not needed
        if (!hasMainExercises) {
            intensityTableLayout.setColumnCollapsed(1, true);
        }

        if (!hasSecondaryExercises) {
            intensityTableLayout.setColumnCollapsed(2, true);
        }

        if (!hasAccessoryExercises) {
            intensityTableLayout.setColumnCollapsed(3, true);
        }

        if (!hasCardioExercises) {
            intensityTableLayout.setColumnCollapsed(4, true);
        }

        // Load intensity
        String[][] intensityPerBlock = currentRoutine.getIntensityPerBlock();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                String[] intensityPerWeek = intensityPerBlock[i][j].split(Pattern.quote("|"));
                int exerciseType = 0;

                for (String intensityPrescription : intensityPerWeek) {
                    String[] intensityDetails = intensityPrescription.split("-");

                    intensityCells[exerciseType][i][j].setText(intensityDetails[1]);

                    if (intensityDetails[0].equals("Main")) {
                        intensityCells[exerciseType][i][j].setTextColor(Color.parseColor("#ffcc0000"));
                    }

                    if (intensityDetails[0].equals("Secondary")) {
                        intensityCells[exerciseType][i][j].setTextColor(Color.parseColor("#ff0099cc"));
                    }

                    if (intensityDetails[0].equals("Accessory")) {
                        intensityCells[exerciseType][i][j].setTextColor(Color.parseColor("#ff669900"));
                    }

                    if (intensityDetails[0].equals("Cardio")) {
                        intensityCells[exerciseType][i][j].setTextColor(Color.BLACK);
                    }

                    ++exerciseType;
                }
            }
        }
    }
}
