package com.example.pothole.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pothole.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private ArrayList<HistoryItem> historyList;
    private Context context;

    public HistoryAdapter(Context context, ArrayList<HistoryItem> historyList) {
        this.context = context;
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

        // Lấy chuỗi từ strings.xml
        String dateTimeLabel = context.getString(R.string.date_time_label);
        String locationLabel = context.getString(R.string.location_label);
        String severityLabel = context.getString(R.string.severity_label);
        String userReportedLabel = context.getString(R.string.user_reported_label);

        // Gán dữ liệu vào các TextView
        holder.tvDateTime.setText(String.format("%s %s", dateTimeLabel, item.getDateTime()));
        holder.tvLocation.setText(String.format("%s %s", locationLabel, item.getLocation()));
        holder.tvSeverity.setText(String.format("%s %s", severityLabel, item.getSeverity()));
        holder.tvTimestamp.setText(String.format("%s %s", userReportedLabel, item.getTimestamp()));

        // Đặt màu nền dựa trên mức độ nghiêm trọng
        int backgroundColor;
        switch (item.getSeverity().toLowerCase()) {
            case "minor":
                backgroundColor = ContextCompat.getColor(context, R.color.severity_minor);
                break;
            case "medium":
                backgroundColor = ContextCompat.getColor(context, R.color.severity_medium);
                break;
            case "severe":
                backgroundColor = ContextCompat.getColor(context, R.color.severity_severe);
                break;
            default:
                backgroundColor = ContextCompat.getColor(context, android.R.color.white);
                break;
        }
        holder.itemView.setBackgroundColor(backgroundColor);
    }
    public void updateList(ArrayList<HistoryItem> newList) {
        historyList.clear();
        historyList.addAll(newList);
        notifyDataSetChanged();
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
