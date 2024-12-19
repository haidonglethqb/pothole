package com.example.pothole.DashboardScreen;

import android.os.Bundle;
import android.util.Log;

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
import com.example.pothole.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyReportPothole extends AppCompatActivity {

    private static final String TAG = "DailyReportPothole";
    private AnyChartView lineChartView;

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

        lineChartView = findViewById(R.id.line_chart_view);

        // Lấy dữ liệu từ Firebase và thiết lập biểu đồ
        fetchPotholeDataFromFirebase();
    }

    private void fetchPotholeDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("potholes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Integer> weeklyData = new HashMap<>();
                // Khởi tạo giá trị mặc định cho từng ngày
                String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                for (String day : daysOfWeek) {
                    weeklyData.put(day, 0);
                }

                // Xử lý dữ liệu từ Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String dateTime = snapshot.child("dateTime").getValue(String.class);
                    Integer potholeCount = snapshot.child("potholeCount").getValue(Integer.class);

                    if (dateTime != null && potholeCount != null) {
                        String dayOfWeek = getDayOfWeek(dateTime);
                        if (weeklyData.containsKey(dayOfWeek)) {
                            weeklyData.put(dayOfWeek, weeklyData.get(dayOfWeek) + potholeCount);
                        }
                    }
                }

                // Chuyển dữ liệu sang định dạng cho AnyChart
                List<DataEntry> chartData = new ArrayList<>();
                for (String day : daysOfWeek) {
                    chartData.add(new ValueDataEntry(day, weeklyData.get(day)));
                }

                setupLineChart(chartData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read data from Firebase", databaseError.toException());
            }
        });
    }

    private String getDayOfWeek(String dateTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(dateTime));
            return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse dateTime: " + dateTime, e);
            return "";
        }
    }

    private void setupLineChart(List<DataEntry> data) {
        Cartesian cartesian = AnyChart.line();
        cartesian.title("Pothole Count for the Week");
        cartesian.xAxis(0).title("Days of the Week");
        cartesian.yAxis(0).title("Number of Potholes");

        Line line = cartesian.line(data);
        lineChartView.setChart(cartesian);
    }
}
