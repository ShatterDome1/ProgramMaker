package com.mpascal.programmaker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mpascal.programmaker.R;
import com.mpascal.programmaker.core.Routine;

import java.util.ArrayList;

// This class is the bridge between the data and the recycler view
// The class will add the items to the recycler view when needed
public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private ArrayList<Routine> routines;
    private OnItemClickListener listener;

    // Implemented by the RoutineFragment in the buildRecyclerView method
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RoutineAdapter(ArrayList<Routine> routines) {
        this.routines = routines;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_routine, parent, false);
        return new RoutineViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine currentRoutine = routines.get(position);

        holder.title.setText(currentRoutine.getTitle());
        holder.goal.setText(currentRoutine.getGoal());

        String daysPerWeek = currentRoutine.getDaysAvailable().size() + " days/week";
        holder.days.setText(daysPerWeek);
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView goal;
        private TextView days;
        private ImageView deleteRoutine;

        RoutineViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.routine_title);
            deleteRoutine = itemView.findViewById(R.id.delete_routine);
            goal = itemView.findViewById(R.id.routine_goal);
            days = itemView.findViewById(R.id.routine_total_days);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            deleteRoutine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}
