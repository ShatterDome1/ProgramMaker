package com.mpascal.programmaker;

import android.content.Context;
import android.content.Intent;
import android.content.PeriodicSync;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.db.User;
import com.mpascal.programmaker.fragments.ProfileFragment;
import com.mpascal.programmaker.fragments.RoutineFragment;
import com.mpascal.programmaker.fragments.SurveyFragment;
import com.mpascal.programmaker.viewmodels.RoutineFragmentViewModel;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SurveyFragment.SurveyFragmentListener {

    public static final String PACKAGE_NAME = "com.mpascal.programmaker";

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private TextView loggedInUser;
    private TextView loggedInEmail;

    // the array will store the days available
    private ArrayList<Integer> daysAvailable;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This will retrieve the days available from the survey
        daysAvailable = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
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
            user = new User(sharedPref.getString("firstName",""),
                    sharedPref.getString("lastName",""),
                    sharedPref.getString("email",""),
                    sharedPref.getString("password",""),
                    sharedPref.getString("dateOfBirth", ""),
                    sharedPref.getString("key", ""));
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
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RoutineFragment()).commit();
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
        editor.putString("password", user.getPassword());
        editor.putString("dateOfBirth", user.getDateOfBirth());
        editor.putString("key", user.getKey());
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:

                // Store the user details in a bundle and pass them to the user profile
                Fragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(PACKAGE_NAME + ".userDetails", user);
                profileFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        profileFragment).commit();
                break;

            case R.id.nav_routines:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RoutineFragment()).commit();
                break;

            case R.id.nav_logout:
                logout();
                break;

            case R.id.nav_share:
                //TODO Share
                Toast.makeText(this, "Share!!!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_download:
                //TODO Download
                Toast.makeText(this, "Download!!", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onSurveyCompleted(String goal, String weight, String height) {

        String[] strDateOfBirth = user.getDateOfBirth().split("/");
        Log.d(TAG, "onSurveyCompleted: " + strDateOfBirth[0] + " " + strDateOfBirth[1] + " " + strDateOfBirth[2]);
        LocalDate currentDate = LocalDate.now();
        LocalDate dateOfBirth = LocalDate.of(Integer.parseInt(strDateOfBirth[2]),
                                             Integer.parseInt(strDateOfBirth[1]),
                                             Integer.parseInt(strDateOfBirth[0]));

        int age = Period.between(dateOfBirth, currentDate).getYears();

        // take strings and send to routine fragment
        RoutineFragment.addRoutine(goal, daysAvailable, weight, height, age);

        // Reinitialise the daysAvailable array so that it's empty when creating
        // a new routine
        daysAvailable = new ArrayList<>();
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
