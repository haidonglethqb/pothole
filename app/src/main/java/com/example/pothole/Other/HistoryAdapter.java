package com.example.pothole.Other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pothole.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private ArrayList<HistoryItem> historyList;

    public HistoryAdapter(ArrayList<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);

        holder.tvDateTime.setText("Date/Time: " + item.getDateTime());
        holder.tvLocation.setText("Location: " + item.getLocation());
        holder.tvSeverity.setText("Severity: " + item.getSeverity());
        holder.tvTimestamp.setText("Timestamp: " + item.getTimestamp());

        // Set background color based on severity
        switch (item.getSeverity()) {
            case "Minor":
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.severity_minor));
                break;
            case "Medium":
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.severity_medium));
                break;
            case "Severe":
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.severity_severe));
                break;
            default:
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateTime, tvLocation, tvSeverity, tvTimestamp;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvSeverity = itemView.findViewById(R.id.tvSeverity);

        }
    }
}