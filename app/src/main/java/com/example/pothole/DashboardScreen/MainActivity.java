package com.example.pothole.DashboardScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.patrykandpatrick.vico.core.entry.ChartEntry;
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer;
import com.patrykandpatrick.vico.views.chart.ChartView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pothole.MapScreen.mapdisplay;
import com.example.pothole.R;
import com.example.pothole.SettingScreen.Settings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.patrykandpatrick.vico.views.chart.ComposedChartView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageButton home_button, maps_button, history_button, settings_button;
    TextView accerleratorX, accerleratorY, accerleratorZ, combinedDelta, tvpotholeCount, tvdistance;

    ChartView recentPotholePieChart;
    ComposedChartView overviewReportLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons
        home_button = findViewById(R.id.home_button);
        maps_button = findViewById(R.id.maps_button);
        history_button = findViewById(R.id.history_button);
        settings_button = findViewById(R.id.settings_button);

        // Initialize TextViews
        accerleratorX = findViewById(R.id.acceleratorX);
        accerleratorY = findViewById(R.id.acceleratorY);
        accerleratorZ = findViewById(R.id.acceleratorZ);
        combinedDelta = findViewById(R.id.combinedDelta);
        tvdistance = findViewById(R.id.traveledDistance);
        tvpotholeCount = findViewById(R.id.potholeCount);

        // Initialize Charts
        recentPotholePieChart = findViewById(R.id.recentPotholePieChart);
        overviewReportLineChart = findViewById(R.id.overviewReportLineChart);

        // Button Click Listeners
        home_button.setOnClickListener(v -> Toast.makeText(MainActivity.this, "You are already on the home screen", Toast.LENGTH_SHORT).show());
        maps_button.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, mapdisplay.class)));
        settings_button.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Settings.class)));
        history_button.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, com.example.pothole.Other.History.class)));

        // Load data into charts
        setupPieChart();
        //setupLineChart();

        // Đăng ký BroadcastReceiver
        IntentFilter filter = new IntentFilter("com.example.pothole.POTHOLE_DETECTED");
        registerReceiver(potholeReceiver, filter);

<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
        // Đọc dữ liệu từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("PotholeData", MODE_PRIVATE);
        int savedPotholeCount = prefs.getInt("potholeCount", 0);
        float savedTravelDistance = prefs.getFloat("totalDistance", 0.0f);
<<<<<<< Updated upstream
        // Hiển thị dữ liệu trên giao diện
        tvpotholeCount.setText(String.format(Locale.getDefault(), "%d", savedPotholeCount));
        tvdistance.setText(String.format(Locale.getDefault(), "%.2f m", savedTravelDistance));

        home_button.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "You are already on the home screen", Toast.LENGTH_SHORT).show();
        });
=======
>>>>>>> Stashed changes

        // Hiển thị dữ liệu trên giao diện
        tvpotholeCount.setText(String.format(Locale.getDefault(), "%d", savedPotholeCount));
        tvdistance.setText(String.format(Locale.getDefault(), "%.2f m", savedTravelDistance));

        // Kết nối và đọc dữ liệu từ Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference potholeRef = database.getReference("potholeData");

        potholeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Giả sử có 1 node duy nhất
                    float deltaX = snapshot.child("deltax").getValue(Float.class);
                    float deltaY = snapshot.child("deltay").getValue(Float.class);
                    float deltaZ = snapshot.child("deltaz").getValue(Float.class);
                    float combinedDeltaValue = snapshot.child("combinedDelta").getValue(Float.class);
                    int potholeCount = snapshot.child("potholeCount").getValue(Integer.class);
                    double totalDistance = snapshot.child("totalDistance").getValue(Double.class);

                    // Cập nhật dữ liệu vào TextView
                    accerleratorX.setText(String.format(Locale.getDefault(), "%.2f", deltaX));
                    accerleratorY.setText(String.format(Locale.getDefault(), "%.2f", deltaY));
                    accerleratorZ.setText(String.format(Locale.getDefault(), "%.2f", deltaZ));
                    combinedDelta.setText(String.format(Locale.getDefault(), "%.2f", combinedDeltaValue));
                    tvpotholeCount.setText(String.format(Locale.getDefault(), "%d", potholeCount));
                    tvdistance.setText(String.format(Locale.getDefault(), "%.2f m", totalDistance));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //Toast.makeText(MainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Biểu đồ tròn thể hiện mức độ nghiêm trọng

    private void setupPieChart() {
        DatabaseReference chartRef = FirebaseDatabase.getInstance().getReference("potholes");

        // Lắng nghe dữ liệu từ Firebase
        // 3 mức độ nghiêm trọng
        chartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Kiểm tra từng child của "potholeData"
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        try {
                            // Lấy dữ liệu từ Firebase
                            Float deltaX = childSnapshot.child("deltax").getValue(Float.class);
                            Float deltaY = childSnapshot.child("deltay").getValue(Float.class);
                            Float deltaZ = childSnapshot.child("deltaz").getValue(Float.class);
                            Float combinedDeltaValue = childSnapshot.child("combinedDelta").getValue(Float.class);
                            Integer potholeCount = childSnapshot.child("potholeCount").getValue(Integer.class);
                            Double totalDistance = childSnapshot.child("totalDistance").getValue(Double.class);
                            String severity = childSnapshot.child("severity").getValue(String.class);

                            // Cập nhật dữ liệu vào giao diện
                            accerleratorX.setText(deltaX != null ? String.format(Locale.getDefault(), "%.2f", deltaX) : "N/A");
                            accerleratorY.setText(deltaY != null ? String.format(Locale.getDefault(), "%.2f", deltaY) : "N/A");
                            accerleratorZ.setText(deltaZ != null ? String.format(Locale.getDefault(), "%.2f", deltaZ) : "N/A");
                            combinedDelta.setText(combinedDeltaValue != null ? String.format(Locale.getDefault(), "%.2f", combinedDeltaValue) : "N/A");
                            tvpotholeCount.setText(potholeCount != null ? String.valueOf(potholeCount) : "N/A");
                            tvdistance.setText(totalDistance != null ? String.format(Locale.getDefault(), "%.2f m", totalDistance) : "N/A");

                            // Log hoặc sử dụng giá trị severity
                            if (severity != null) {
                                Toast.makeText(MainActivity.this, "Severity: " + severity, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void setupLineChart() {
//        // Sample Line Chart Data (Replace this with actual data from Firebase)
//        List<ChartEntry> lineEntries = new ArrayList<>();
//        for (int i = 0; i < 7; i++) {
//            lineEntries.add(new ChartEntry(i, (float) (Math.random() * 10)));
//        }
//
//        // Bind data to the ComposedChartView
//        ChartEntryModelProducer producer = new ChartEntryModelProducer(lineEntries);
//        overviewReportLineChart.setModel(producer.getModel());
//    }

    private final BroadcastReceiver potholeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("com.example.pothole.POTHOLE_DETECTED")) {
                float deltaX = intent.getFloatExtra("deltaX", 0);
                float deltaY = intent.getFloatExtra("deltaY", 0);
                float deltaZ = intent.getFloatExtra("deltaZ", 0);
                float combinedDelta = intent.getFloatExtra("combinedDelta", 0);
                int potholeCount = intent.getIntExtra("potholeCount", 0);
                double totalDistance = intent.getDoubleExtra("totalDistance", 0);

                accerleratorX.setText(String.format(Locale.getDefault(), "%.2f", deltaX));
                accerleratorY.setText(String.format(Locale.getDefault(), "%.2f", deltaY));
                accerleratorZ.setText(String.format(Locale.getDefault(), "%.2f", deltaZ));
                MainActivity.this.combinedDelta.setText(String.format(Locale.getDefault(), "%.2f", combinedDelta));
                tvpotholeCount.setText(String.format(Locale.getDefault(), "%d", potholeCount));
                tvdistance.setText(String.format(Locale.getDefault(), "%.2f m", totalDistance));

//
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(potholeReceiver);
    }
}