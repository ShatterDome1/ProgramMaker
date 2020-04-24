package com.mpascal.programmaker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mpascal.programmaker.MainActivity;
import com.mpascal.programmaker.R;
import com.mpascal.programmaker.RoutineViewActivity;
import com.mpascal.programmaker.adapters.RoutineAdapter;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.db.UserDB;
import com.mpascal.programmaker.viewmodels.RoutineFragmentViewModel;

import java.util.ArrayList;

public class RoutineFragment extends Fragment {
    private static final String TAG = "RoutineFragment";
    public static final String PACKAGE_NAME = "com.mpascal.programmaker.fragments";

    private RecyclerView recyclerView;
    private static RoutineAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ImageView noRoutinesImage;

    private UserDB currentUser;

    private ViewModelProvider viewModelProvider;
    private RoutineFragmentViewModel routineFragmentViewModel;

    private FloatingActionButton createRoutine;

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routines, container, false);

        progressBar = view.findViewById(R.id.routines_progress_bar);
        progressBar.setVisibility(View.GONE);

        noRoutinesImage = view.findViewById(R.id.no_routines_image);
        noRoutinesImage.setVisibility(View.GONE);

        // Get the current user
        final Bundle bundle = getArguments();
        currentUser = bundle.getParcelable(MainActivity.PACKAGE_NAME + ".userDetails");

        viewModelProvider = new ViewModelProvider(this);
        routineFragmentViewModel = viewModelProvider.get(RoutineFragmentViewModel.class);

        routineFragmentViewModel.init(currentUser.getEmail());

        routineFragmentViewModel.getIsDeletingRoutine().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == -1) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (integer == -2) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Delete routine failed", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyItemRemoved(integer);
                    
                    if (adapter.getItemCount() == 0) {
                        noRoutinesImage.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        buildRecyclerView(view);

        createRoutine = view.findViewById(R.id.fragment_create_routine);
        createRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SurveyFragment surveyFragment = new SurveyFragment();
                surveyFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, surveyFragment, "SurveyFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void buildRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(view.getContext());

        adapter = new RoutineAdapter(routineFragmentViewModel.getRoutines().getValue());

        if (adapter.getItemCount() == 0) {
            noRoutinesImage.setVisibility(View.VISIBLE);
        }

        routineFragmentViewModel.getIsFetchingData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, "onChanged: data is being fetched");
                    progressBar.setVisibility(View.VISIBLE);
                    noRoutinesImage.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "onChanged: notified");
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    // Show that the user doesn't have any routines
                    if (adapter.getItemCount() == 0) {
                        noRoutinesImage.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RoutineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), RoutineViewActivity.class);
                Routine currentRoutine = routineFragmentViewModel.getRoutine(position);
                intent.putExtra(PACKAGE_NAME + ".routine", currentRoutine);
                intent.putExtra(PACKAGE_NAME + ".user", currentUser);
                intent.putExtra(PACKAGE_NAME + ".routinePosition", position);
                startActivity(intent);
                Log.d(TAG, "onItemClick: " + currentRoutine.getTitle());
            }

            @Override
            public void onDeleteClick(int position) {
                Log.d(TAG, "onDeleteClick: " + position);
                routineFragmentViewModel.deleteRoutine(currentUser.getEmail(), position);
            }
        });
    }
}
