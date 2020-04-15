package com.mpascal.programmaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mpascal.programmaker.db.UserDB;
import com.mpascal.programmaker.dialogs.ChangePasswordDialog;
import com.mpascal.programmaker.dialogs.DeleteAccountDialog;
import com.mpascal.programmaker.fragments.ProfileFragment;
import com.mpascal.programmaker.fragments.RoutineFragment;
import com.mpascal.programmaker.fragments.SurveyFragment;
import com.mpascal.programmaker.repositories.RoutineRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SurveyFragment.SurveyFragmentListener,
        ChangePasswordDialog.ChangePasswordDialogListener,
        DeleteAccountDialog.DeleteAccountDialogListener {

    public static final String PACKAGE_NAME = "com.mpascal.programmaker";

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private TextView loggedInUser;
    private TextView loggedInEmail;

    // the array will store the days available
    private ArrayList<Integer> daysAvailable;

    private UserDB user;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This will retrieve the days available from the survey
        daysAvailable = new ArrayList<>();

        // Set the custom toolbar so that the navigation menu foes over it
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#FF6200EE"));
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Get the user details either from the incoming LoginActivity intent
        // or from the shared preferences set when the activity calls onPause()

        Intent intent = getIntent();
        if (intent.hasExtra(LoginActivity.PACKAGE_NAME + ".userDetails")) {
            user = intent.getParcelableExtra(LoginActivity.PACKAGE_NAME + ".userDetails");
        } else {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            user = new UserDB(sharedPref.getString("firstName",""),
                    sharedPref.getString("lastName",""),
                    sharedPref.getString("email",""),
                    sharedPref.getString("dateOfBirth", ""));
        }

        // Set the logged in user details
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        String userFullName = user.getFirstName() + " " + user.getLastName();
        loggedInUser = headerView.findViewById(R.id.logged_in_user);
        loggedInEmail = headerView.findViewById(R.id.logged_in_email);
        loggedInUser.setText(userFullName);
        loggedInEmail.setText(user.getEmail());

        // Swap fragments
        navigationView.setNavigationItemSelectedListener(this);

        // Set the initial fragment shown
        Bundle bundle = new Bundle();
        bundle.putParcelable(PACKAGE_NAME + ".userDetails", user);

        Fragment routineFragment = new RoutineFragment();
        routineFragment.setArguments(bundle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    routineFragment, "RoutineFragment").commit();
            navigationView.setCheckedItem(R.id.nav_routines);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if ( currentFragment != null &&
                    currentFragment.getClass() == SurveyFragment.class &&
                    !daysAvailable.isEmpty()) {
                daysAvailable = new ArrayList<>();
            } else if (currentFragment.getClass() != SurveyFragment.class) {
                // When the user signs out, clear the data from the RoutinesRepository
                RoutineRepository routineRepository = RoutineRepository.getInstance();
                routineRepository.clearData();

                auth.signOut();
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("email", user.getEmail());
        editor.putString("dateOfBirth", user.getDateOfBirth());
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PACKAGE_NAME + ".userDetails", user);

        switch (menuItem.getItemId()) {

            case R.id.nav_profile:
                // Store the user details in a bundle and pass them to the user profile
                Fragment profileFragment = new ProfileFragment();
                profileFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        profileFragment, "ProfileFragment").commit();
                break;

            case R.id.nav_routines:
                // Store the user details in a bundle and pass them to the user profile
                Fragment routineFragment = new RoutineFragment();
                routineFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        routineFragment, "RoutineFragment").commit();
                break;

            case R.id.nav_logout:
                logout();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onSurveyCompleted(String goal, String weight, String height) {

        String[] strDateOfBirth = user.getDateOfBirth().split("/");
        Log.d(TAG, "onSurveyCompleted: " + strDateOfBirth[0] + " " + strDateOfBirth[1] + " " + strDateOfBirth[2]);
        LocalDate currentDate = LocalDate.now();
        LocalDate dateOfBirth = LocalDate.of(Integer.parseInt(strDateOfBirth[2]),
                                             Integer.parseInt(strDateOfBirth[1]),
                                             Integer.parseInt(strDateOfBirth[0]));

        int age = Period.between(dateOfBirth, currentDate).getYears();

        boolean ok = true;


        if (daysAvailable.size() != 0) {
            // If the user has picked 7 days then remove Wednesday and and create the routine
            if (daysAvailable.size() == 7) {
                daysAvailable.remove((Integer) 3);
            }

            // If the user picks 6 days and if the goal is not Hypertrophy, force the routine split
            // to be calculated to FB+GPP
            if (daysAvailable.size() == 6 && !goal.equals("Hypertrophy")) {
                // Check which day is missing
                int missingDay = -1;
                for (int i =0; i < 7; i++) {
                    boolean isMissing= daysAvailable.contains(i);
                    if (isMissing) {
                        missingDay = i;
                        break;
                    }
                }

                // Remove the
                switch (missingDay) {
                    case 0:
                        daysAvailable.remove((Integer) 5);
                        daysAvailable.remove((Integer) 2);
                        break;

                    case 1:
                        daysAvailable.remove((Integer) 6);
                        daysAvailable.remove((Integer) 3);
                        break;

                    case 2:
                        daysAvailable.remove((Integer) 0);
                        daysAvailable.remove((Integer) 4);

                    case 3:
                        daysAvailable.remove((Integer) 1);
                        daysAvailable.remove((Integer) 5);
                        break;

                    case 4:
                        daysAvailable.remove((Integer) 2);
                        daysAvailable.remove((Integer) 6);
                        break;

                    case 5:
                        daysAvailable.remove((Integer) 3);
                        daysAvailable.remove((Integer) 1);
                        break;

                    case 6:
                        daysAvailable.remove((Integer) 4);
                        daysAvailable.remove((Integer) 2);
                        break;
                }
            }

            // take strings and send to routine fragment
            SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentByTag("SurveyFragment");
            fragment.addRoutine(goal, daysAvailable, weight, height, age);

            // Reinitialise the daysAvailable array so that it's empty when creating
            // a new routine
            daysAvailable = new ArrayList<>();
        } else {
            Toast.makeText(this, "Please select days that you can train", Toast.LENGTH_SHORT).show();
            ok = false;
        }

        return ok;
    }

    // This will handle the checkbox ticks in the Survey Fragment Days available question
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.survey_monday:
                if (checked)
                    daysAvailable.add(0);
                else
                    daysAvailable.remove((Integer) 0);
                break;

            case R.id.survey_tuesday:
                if (checked)
                    daysAvailable.add(1);
                else
                    daysAvailable.remove((Integer) 1);
                break;

            case R.id.survey_wednesday:
                if (checked)
                    daysAvailable.add(2);
                else
                    daysAvailable.remove((Integer) 2);
                break;

            case R.id.survey_thursday:
                if (checked)
                    daysAvailable.add(3);
                else
                    daysAvailable.remove((Integer) 3);
                break;

            case R.id.survey_friday:
                if (checked)
                    daysAvailable.add(4);
                else
                    daysAvailable.remove((Integer) 4);
                break;

            case R.id.survey_saturday:
                if (checked)
                    daysAvailable.add(5);
                else
                    daysAvailable.remove((Integer) 5);
                break;

            case R.id.survey_sunday:
                if (checked)
                    daysAvailable.add(6);
                else
                    daysAvailable.remove((Integer) 6);
                break;
        }
    }

    private void logout() {
        auth.signOut();

        // When the user signs out, clear the data from the RoutinesRepository
        RoutineRepository routineRepository = RoutineRepository.getInstance();
        routineRepository.clearData();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // This action happens in the profile fragment but the base activity is MainActivity, so the
    // dialog listeners logic must be implemented here
    @Override
    public void changePassword(String password, final String newPassStr, String newPassConfStr) {
        if (newPassConfStr.equals(newPassStr)) {
            final FirebaseUser currentUser = auth.getCurrentUser();

            // Get auth credentials from the user for re-authentication. The example below shows
            // email and password credentials but there are multiple possible providers,
            // such as GoogleAuthProvider or FacebookAuthProvider.
            AuthCredential credential = EmailAuthProvider
                    .getCredential(currentUser.getEmail(), password);

            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        currentUser.updatePassword(newPassStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onComplete: password updated");
                                } else {
                                    Toast.makeText(MainActivity.this, "Password Update Failed", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onComplete: ", task.getException());
                                }
                            }
                        });
                    } else {
                        Toast.makeText( MainActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "New Passwords Don't Match!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteAccount(String password) {
        final FirebaseUser currentUser = auth.getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(currentUser.getEmail(), password);

        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    db.collection("Routines").whereEqualTo("email", currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // Delete Routines
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                db.collection("Routines").document(documentSnapshot.getId()).delete().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: failed" + e.toString());
                                    }
                                });
                            }

                            // Delete user record from database
                            db.collection("Users").document(currentUser.getEmail()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    // Delete Authentication user
                                    currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                auth.signOut();
                                                Log.d(TAG, "onComplete: Account Deleted");
                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            } else {
                                                Log.d(TAG, "onComplete: failed", task.getException());
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: failed" + e.toString());
                                }
                            });
                        }
                    });
                } else {
                    Log.d(TAG, "onComplete: reauth failed", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
