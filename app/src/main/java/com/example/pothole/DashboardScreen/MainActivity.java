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

import androidx.appcompat.app.AppCompatActivity;
import com.patrykandpatrick.vico.core.entry.ChartEntry;
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer;
import com.patrykandpatrick.vico.views.chart.ChartView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
        setupLineChart();

        // Đăng ký BroadcastReceiver
        IntentFilter filter = new IntentFilter("com.example.pothole.POTHOLE_DETECTED");
        registerReceiver(potholeReceiver, filter);


        // Đọc dữ liệu từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("PotholeData", MODE_PRIVATE);
        int savedPotholeCount = prefs.getInt("potholeCount", 0);
        float savedTravelDistance = prefs.getFloat("totalDistance", 0.0f);
        // Hiển thị dữ liệu trên giao diện
        tvpotholeCount.setText(String.format(Locale.getDefault(), "%d", savedPotholeCount));
        tvdistance.setText(String.format(Locale.getDefault(), "%.2f m", savedTravelDistance));

        home_button.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "You are already on the home screen", Toast.LENGTH_SHORT).show();
        });

        maps_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, mapdisplay.class);
            startActivity(intent);
        });

        settings_button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        });

        history_button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.pothole.Other.History.class);
            startActivity(intent);
        });

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

    private void setupPieChart() {
        // Sample Pie Chart Data (Replace this with actual data from Firebase)
        List<ChartEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new ChartEntry(0, 30f)); // Severity level 1
        pieEntries.add(new ChartEntry(1, 50f)); // Severity level 2
        pieEntries.add(new ChartEntry(2, 20f)); // Severity level 3

        // Bind data to the ChartView
        ChartEntryModelProducer producer = new ChartEntryModelProducer(pieEntries);
        recentPotholePieChart.setModel(producer.getModel());
    }

    private void setupLineChart() {
        // Sample Line Chart Data (Replace this with actual data from Firebase)
        List<ChartEntry> lineEntries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            lineEntries.add(new ChartEntry(i, (float) (Math.random() * 10)));
        }

        // Bind data to the ComposedChartView
        ChartEntryModelProducer producer = new ChartEntryModelProducer(lineEntries);
        overviewReportLineChart.setModel(producer.getModel());
    }



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
