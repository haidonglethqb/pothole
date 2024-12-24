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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class History extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private ArrayList<HistoryItem> historyList;
    private ArrayList<HistoryItem> originalList;
    private Spinner spinnerSeverity, spinnerTimeframe;
    private ImageButton home_button, maps_button, history_button, settings_button, back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

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
                filterHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerTimeframe = findViewById(R.id.timeframe_spinner);
        ArrayAdapter<CharSequence> timeframeAdapter = ArrayAdapter.createFromResource(this,
                R.array.timeframes, android.R.layout.simple_spinner_item);
        timeframeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeframe.setAdapter(timeframeAdapter);

        spinnerTimeframe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("potholes");
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
                                Log.d("Firebase", "Item added: " + item.toString());
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

    private void filterHistory() {
        String selectedSeverity = spinnerSeverity.getSelectedItem().toString();
        String selectedTimeframe = spinnerTimeframe.getSelectedItem().toString();

        ArrayList<HistoryItem> filteredList = new ArrayList<>();
        for (HistoryItem item : originalList) {
            boolean matchesSeverity = item.getSeverity().equals(selectedSeverity) || selectedSeverity.equals("All");
            boolean matchesTimeframe = selectedTimeframe.equals("All") ||
                    (selectedTimeframe.equals("This Week") && isCurrentWeek(item.getDateTime())) ||
                    (selectedTimeframe.equals("This Month") && isCurrentMonth(item.getDateTime()));

            if (matchesSeverity && matchesTimeframe) {
                filteredList.add(item);
            }
        }
        historyAdapter.updateList(filteredList);
    }

    private boolean isCurrentWeek(String dateTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Calendar given = Calendar.getInstance();
            given.setTime(format.parse(dateTime));

            Calendar current = Calendar.getInstance();
            current.set(Calendar.HOUR_OF_DAY, 0);
            current.set(Calendar.MINUTE, 0);
            current.set(Calendar.SECOND, 0);
            current.set(Calendar.MILLISECOND, 0);
            current.set(Calendar.DAY_OF_WEEK, current.getFirstDayOfWeek());

            given.set(Calendar.HOUR_OF_DAY, 0);
            given.set(Calendar.MINUTE, 0);
            given.set(Calendar.SECOND, 0);
            given.set(Calendar.MILLISECOND, 0);
            given.set(Calendar.DAY_OF_WEEK, given.getFirstDayOfWeek());

            return given.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
                    given.get(Calendar.WEEK_OF_YEAR) == current.get(Calendar.WEEK_OF_YEAR);
        } catch (Exception e) {
            Log.e("History", "Error parsing date: " + dateTime, e);
            return false;
        }
    }

    private boolean isCurrentMonth(String dateTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Calendar given = Calendar.getInstance();
            given.setTime(format.parse(dateTime));

            Calendar current = Calendar.getInstance();

            return given.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
                    given.get(Calendar.MONTH) == current.get(Calendar.MONTH);
        } catch (Exception e) {
            Log.e("History", "Error parsing date: " + dateTime, e);
            return false;
        }
    }
}