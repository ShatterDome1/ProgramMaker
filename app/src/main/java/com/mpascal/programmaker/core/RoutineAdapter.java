package com.mpascal.programmaker.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mpascal.programmaker.R;

import java.util.ArrayList;

// This class is the bridge between the data and the recycler view
// The class will add the items to the recycler view when needed
public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private ArrayList<Routine> routines;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class RoutineViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public ImageView deleteRoutine;

        public RoutineViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.routine_title);
            description = itemView.findViewById(R.id.routine_description);
            deleteRoutine = itemView.findViewById(R.id.delete_routine);

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

    public RoutineAdapter(ArrayList<Routine> routines) {
        this.routines = routines;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.routine, parent, false);
        RoutineViewHolder routineViewHolder = new RoutineViewHolder(v, listener);
        return routineViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine currentRoutine = routines.get(position);

        holder.title.setText(currentRoutine.getTitle());
        holder.description.setText(currentRoutine.getText());
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }
}
