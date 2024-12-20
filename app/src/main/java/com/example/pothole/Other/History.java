package com.example.pothole.Other;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
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
    private ArrayList<HistoryItem> originalList;
    private Spinner spinnerSeverity;

    private ImageButton home_button, maps_button, history_button, settings_button, back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.rvHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        originalList = new ArrayList<>();

        historyAdapter = new HistoryAdapter(this, historyList);
        recyclerView.setAdapter(historyAdapter);

        spinnerSeverity = findViewById(R.id.severityFilter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.severity_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeverity.setAdapter(adapter);

        spinnerSeverity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSeverity = parent.getItemAtPosition(position).toString();
                filterHistoryBySeverity(selectedSeverity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("potholes");
        Query checkUserDB = databaseRef.orderByChild("timestamp");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                originalList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            HistoryItem item = dataSnapshot.getValue(HistoryItem.class);
                            if (item != null) {
                                historyList.add(item);
                                originalList.add(item);
                            }
                        } catch (Exception e) {
                            Log.e("Firebase", "Error parsing data: ", e);
                        }
                    }
                    historyAdapter.notifyDataSetChanged();
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

        home_button.setOnClickListener(v -> {
            Intent intent = new Intent(History.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        maps_button.setOnClickListener(view -> {
            Intent intent = new Intent(History.this, mapdisplay.class);
            startActivity(intent);
            finish();
        });

        settings_button.setOnClickListener(v -> {
            Intent intent = new Intent(History.this, Settings.class);
            startActivity(intent);
            finish();
        });

        history_button.setOnClickListener(v ->
                Toast.makeText(History.this, "You are already on the history screen", Toast.LENGTH_SHORT).show()
        );

        back_button.setOnClickListener(v -> finish());
    }

    private void filterHistoryBySeverity(String severity) {
        ArrayList<HistoryItem> filteredList = new ArrayList<>();
        for (HistoryItem item : originalList) {
            if (item.getSeverity().equals(severity) || severity.equals("All")) {
                filteredList.add(item);
            }
        }
        historyAdapter.updateList(filteredList);
    }
}