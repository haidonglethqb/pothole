package com.example.pothole.DashboardScreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.scales.Linear;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyReportPothole extends AppCompatActivity {

    // UI elements
    ImageButton home_button, maps_button, history_button, settings_button, btnBack;
    private static final String TAG = "DailyReportPothole";
    private static final String WEEK_VIEW = "This week";
    private static final String MONTH_VIEW = "This month";
    private Spinner timeframe_spinner;
    private AnyChartView lineChartView;
    private Cartesian cartesian;
    private TextView total_potholes, large_potholes, medium_potholes, small_potholes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_report_pothole);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        home_button = findViewById(R.id.home_button);
        maps_button = findViewById(R.id.maps_button);
        history_button = findViewById(R.id.history_button);
        settings_button = findViewById(R.id.settings_button);
        lineChartView = findViewById(R.id.line_chart_view);
        timeframe_spinner = findViewById(R.id.timeframe_spinner);
        total_potholes = findViewById(R.id.total_potholes);
        large_potholes = findViewById(R.id.large_potholes);
        medium_potholes = findViewById(R.id.medium_potholes);
        small_potholes = findViewById(R.id.small_potholes);

        // Initialize the chart once
        cartesian = AnyChart.line();
        cartesian.title("Pothole Count");
        cartesian.yAxis(0).title("No. of Potholes");

        // Sự kiện click vào nút Home, Maps, History, Settings
        home_button.setOnClickListener(view -> {
            Intent intent = new Intent(DailyReportPothole.this, MainActivity.class);
            startActivity(intent);
        });
        maps_button.setOnClickListener(view -> {
            Intent intent = new Intent(DailyReportPothole.this, mapdisplay.class);
            startActivity(intent);
        });
        settings_button.setOnClickListener(v -> {
            Intent intent = new Intent(DailyReportPothole.this, Settings.class);
            startActivity(intent);
        });
        history_button.setOnClickListener(v -> {
            Intent intent = new Intent(DailyReportPothole.this, com.example.pothole.Other.History.class);
            startActivity(intent);
        });

        // Use btnBack to get back to the previous activity
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Khởi tạo spinner
        timeframe_spinner = findViewById(R.id.timeframe_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList(WEEK_VIEW, MONTH_VIEW));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeframe_spinner.setAdapter(adapter);

        // Thêm listener cho spinner
        timeframe_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchPotholeDataFromFirebase();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Lấy dữ liệu từ Firebase và thiết lập biểu đồ
        fetchPotholeDataFromFirebase();
    }

    private void fetchPotholeDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("potholes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int totalPotholes = 0;
                int largePotholes = 0;
                int mediumPotholes = 0;
                int smallPotholes = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    totalPotholes++;
                    String severity = snapshot.child("severity").getValue(String.class);
                    if (severity != null) {
                        switch (severity) {
                            case "Severe":
                                largePotholes++;
                                break;
                            case "Medium":
                                mediumPotholes++;
                                break;
                            case "Minor":
                                smallPotholes++;
                                break;
                        }
                    }
                }

                total_potholes.setText(String.valueOf(totalPotholes));
                large_potholes.setText(String.valueOf(largePotholes));
                medium_potholes.setText(String.valueOf(mediumPotholes));
                small_potholes.setText(String.valueOf(smallPotholes));

                String selectedTimeframe = timeframe_spinner.getSelectedItem().toString();

                if (selectedTimeframe.equals(WEEK_VIEW)) {
                    processWeeklyData(dataSnapshot);
                } else {
                    processMonthlyData(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read data from Firebase", databaseError.toException());
            }
        });
    }

    private void processWeeklyData(DataSnapshot dataSnapshot) {
        Map<String, Integer> weeklyData = new HashMap<>();
        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        // Initialize default values
        for (String day : daysOfWeek) {
            weeklyData.put(day, 0);
        }

        processDataPoints(dataSnapshot, weeklyData, true);

        // Create data for the chart
        List<DataEntry> chartData = new ArrayList<>();
        String currentDay = getDayOfWeek(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                .format(Calendar.getInstance().getTime()));

        for (String day : daysOfWeek) {
            String color = day.equals(currentDay) ? "#FF0000" : "#000000";
            chartData.add(new CustomDataEntry(day, weeklyData.get(day), color));
        }

        setupLineChart(chartData, "Weekly Pothole Count", "Weekdays");
    }

    private void processMonthlyData(DataSnapshot dataSnapshot) {
        Map<String, Integer> monthlyData = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Initialize default values for each day of the month
        for (int i = 1; i <= daysInMonth; i++) {
            monthlyData.put(String.format("%02d", i), 0); // Ensure 2-digit format for the day
        }

        processDataPoints(dataSnapshot, monthlyData, false);

        // Create data for the chart
        List<DataEntry> chartData = new ArrayList<>();
        String currentDay = String.format("%02d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        for (int i = 1; i <= daysInMonth; i++) {
            String dayKey = String.format("%02d", i);
            String color = dayKey.equals(currentDay) ? "#FF0000" : "#000000";
            chartData.add(new CustomDataEntry(dayKey, monthlyData.get(dayKey), color));
        }

        setupMonthlyChart(chartData);
    }

    private void processDataPoints(DataSnapshot dataSnapshot, Map<String, Integer> data, boolean isWeekly) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            try {
                String dateTime = snapshot.child("dateTime").getValue(String.class);
                if (dateTime == null) continue;

                // Kiểm tra thời gian phù hợp
                if (isWeekly && !isCurrentWeek(dateTime)) continue;
                if (!isWeekly && !isCurrentMonth(dateTime)) continue;

                String key;
                if (isWeekly) {
                    key = getDayOfWeek(dateTime);
                } else {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.ENGLISH);
                    key = dayFormat.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(dateTime));
                }

                if (data.containsKey(key)) {
                    data.put(key, data.get(key) + 1);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing data point", e);
            }
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
            Log.e(TAG, "Error parsing date: " + dateTime, e);
            return false;
        }
    }

    // Kiểm tra xem ngày given có phải trong tuần hiện tại không
    private boolean isCurrentWeek(String dateTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Calendar given = Calendar.getInstance();
            given.setTime(format.parse(dateTime));

            Calendar current = Calendar.getInstance();

            // Reset current về đầu tuần
            current.set(Calendar.HOUR_OF_DAY, 0);
            current.set(Calendar.MINUTE, 0);
            current.set(Calendar.SECOND, 0);
            current.set(Calendar.MILLISECOND, 0);
            current.set(Calendar.DAY_OF_WEEK, current.getFirstDayOfWeek());

            // Reset given về đầu tuần
            given.set(Calendar.HOUR_OF_DAY, 0);
            given.set(Calendar.MINUTE, 0);
            given.set(Calendar.SECOND, 0);
            given.set(Calendar.MILLISECOND, 0);
            given.set(Calendar.DAY_OF_WEEK, given.getFirstDayOfWeek());

            // So sánh hai ngày
            return given.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
                    given.get(Calendar.WEEK_OF_YEAR) == current.get(Calendar.WEEK_OF_YEAR);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + dateTime, e);
            return false;
        }
    }

    // Hàm lấy tên ngày trong tuần
    private String getDayOfWeek(String dateTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(dateTime));
            return new SimpleDateFormat("EEE", Locale.ENGLISH).format(calendar.getTime());
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + dateTime, e);
            return "";
        }
    }

    private void setupLineChart(List<DataEntry> data, String title, String xAxisTitle) {
        Cartesian cartesian = AnyChart.line();
        cartesian.title(title);
        cartesian.xAxis(0).title(xAxisTitle);
        cartesian.yAxis(0).title("No. of Potholes");

        Linear yScale = Linear.instantiate();
        yScale.minimum(0d);
        yScale.ticks().interval(1d);
        cartesian.yScale(yScale);

        Line line = cartesian.line(data);
        line.markers().enabled(true);
        line.markers().type("circle").size(4d);

        cartesian.tooltip().positionMode("point");
        lineChartView.setChart(cartesian);
    }

    private void setupMonthlyChart(List<DataEntry> data) {
        Cartesian cartesian = AnyChart.line();
        cartesian.title("Monthly Pothole Count");
        cartesian.xAxis(0).title("Day of Month");
        cartesian.yAxis(0).title("No. of Potholes");

        // Configure X-axis
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        cartesian.xAxis(0).labels().rotation(-45); // Rotate labels to avoid overlap
        cartesian.xAxis(0).ticks().stroke("#000000", 1d, "", "", "");

        // Configure Y-axis
        Linear yScale = Linear.instantiate();
        yScale.minimum(0d);
        yScale.ticks().interval(1d);
        cartesian.yScale(yScale);

        // Configure line
        Line line = cartesian.line(data);
        line.stroke("#1976D2", 2d, "", "", "");
        line.markers().enabled(true);
        line.markers().type("circle").size(4d);

        // Configure tooltip
        cartesian.tooltip()
                .positionMode("point")
                .anchor("right-top")
                .offsetX(5d)
                .offsetY(5d)
                .format("Day: {%X}\nPotholes: {%Value}");

        // Set chart to view
        lineChartView.setChart(cartesian);
    }
}

class CustomDataEntry extends ValueDataEntry {
    CustomDataEntry(String x, Number value, String color) {
        super(x, value);
        setValue("html", true);
        setValue("fill", color.equals("#FF0000") ? "#FFCDD2" : color);
    }
}