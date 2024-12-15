package com.example.pothole.Other;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pothole.DashboardScreen.MainActivity;
import com.example.pothole.MapScreen.mapdisplay;
import com.example.pothole.R;
import com.example.pothole.SettingScreen.Settings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private ArrayList<HistoryItem> historyList;

    // Khai báo các item_navigation_bar
    private ImageButton home_button, maps_button, history_button, settings_button, back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history); // File activity_history.xml

        // Ánh xạ RecyclerView
        recyclerView = findViewById(R.id.rvHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();

        // Adapter
        historyAdapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(historyAdapter);

        // Kết nối Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("potholes");
        Query checkUserDB = databaseRef.orderByChild("timestamp");

        // Lắng nghe dữ liệu từ Firebase
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear(); // Xóa dữ liệu cũ
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.d("Firebase", "Data snapshot: " + dataSnapshot.toString());
                        try {
                            HistoryItem item = dataSnapshot.getValue(HistoryItem.class);
                            if (item != null) {
                                historyList.add(item);
                            } else {
                                Log.e("Firebase", "HistoryItem is null");
                            }
                        } catch (Exception e) {
                            Log.e("Firebase", "Error parsing data: ", e);
                        }
                    }
                    historyAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                } else {
                    Log.w("Firebase", "No data exists in 'History'");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage(), error.toException());
            }
        });


        home_button = findViewById(R.id.home_button);
        maps_button = findViewById(R.id.maps_button);
        history_button = findViewById(R.id.history_button);
        settings_button = findViewById(R.id.settings_button);
        back_button = findViewById(R.id.btnBack);

        // Sự kiện cho item_navigation_bar
        home_button.setOnClickListener(v -> {
            Intent intent = new Intent(History.this, MainActivity.class);
            startActivity(intent);
        });

        maps_button.setOnClickListener(view -> {
            Intent intent = new Intent(History.this, mapdisplay.class);
            startActivity(intent);
        });

        settings_button.setOnClickListener(v -> {
            Intent intent = new Intent(History.this, Settings.class);
            startActivity(intent);
        });

        history_button.setOnClickListener(v -> {
            Toast.makeText(History.this, "You are already on the history screen", Toast.LENGTH_SHORT).show();
        });

        back_button.setOnClickListener(v -> {
            finish();
        });

    }
}