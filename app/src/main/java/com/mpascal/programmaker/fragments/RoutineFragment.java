package com.mpascal.programmaker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mpascal.programmaker.R;
import com.mpascal.programmaker.core.Routine;
import com.mpascal.programmaker.core.RoutineAdapter;

import java.util.ArrayList;

public class RoutineFragment extends Fragment {
    private static final String TAG = "RoutineFragment";
    private RecyclerView recyclerView;
    private RoutineAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Routine> routines = new ArrayList<>();
    private boolean isRoutinesInit = false;

    private ImageView createRoutine;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_routines, container, false);

        if (!isRoutinesInit) {
            routines.add(new Routine("Hypertrophy", "Do this too many times"));
            routines.add(new Routine("Strength", "Do this several times"));
            routines.add(new Routine("Endurance", "Do this wow times"));
            isRoutinesInit = true;
        }

        buildRecyclerView(view);

        createRoutine = view.findViewById(R.id.create_routine);
        createRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SurveyFragment()).commit();
            }
        });

        return view;
    }

    public void addRoutine(String title, String description) {
        routines.add(0, new Routine(title, description));
        adapter.notifyItemInserted(0);
    }

    private void buildRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(view.getContext());
        adapter = new RoutineAdapter(routines);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RoutineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "onItemClick: " + position);
                routines.get(position).setTitle("Clicked");
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onDeleteClick(int position) {
                Log.d(TAG, "onDeleteClick: " + position);
                routines.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
    }
}
