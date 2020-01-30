package com.mpascal.programmaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mpascal.programmaker.db.User;
import com.mpascal.programmaker.fragments.ProfileFragment;
import com.mpascal.programmaker.fragments.RoutineFragment;
import com.mpascal.programmaker.fragments.SurveyFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                               SurveyFragment.SurveyFragmentListener {

    private DrawerLayout drawerLayout;
    private TextView loggedInUser;
    private TextView loggedInEmail;

    // Instance of fragment so that we can send information between them
    private RoutineFragment routineFragment;


    public static final String EXTRA_MESSAGE = "com.example.programmaker.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create new fragment and always use this instead of always creating it again
        routineFragment = new RoutineFragment();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Get the intent from LoginActivity
        Intent intent = getIntent();

        // Create user object and set user details in header
        // index 0 = firstNameDec
        // index 1 = lastNameDec
        // index 2 = emailDec
        // index 3 = passwordDec
        // index 4 = dateOfBirthDec
        // index 5 = key
        String[] userDetails = intent.getStringArrayExtra("userDetails");
        User user = new User(userDetails[0],
                             userDetails[1],
                             userDetails[2],
                             userDetails[3],
                             userDetails[4],
                             userDetails[5]);

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
        if ( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    routineFragment).commit();
            navigationView.setCheckedItem(R.id.nav_routines);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch ( menuItem.getItemId() ) {
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;

            case R.id.nav_routines:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        routineFragment).commit();
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
    public void onBackPressed() {
        if ( drawerLayout.isDrawerOpen(GravityCompat.START) ) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSurveyCompleted(String title, String description) {
        // take strings and send to routine fragment
        routineFragment.addRoutine(title, description);
    }

    private void logout() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public RoutineFragment getRoutineFragment() {
        return routineFragment;
    }
}
