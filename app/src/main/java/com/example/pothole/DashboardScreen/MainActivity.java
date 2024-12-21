package com.example.pothole.DashboardScreen;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.AnyChartView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pothole.MapScreen.mapdisplay;
import com.example.pothole.R;
import com.example.pothole.SettingScreen.BaseActivity;
import com.example.pothole.SettingScreen.EditProfile;
import com.example.pothole.SettingScreen.Settings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements LocationListener {

    ImageButton home_button, maps_button, history_button, settings_button;
    TextView accerleratorX, accerleratorY, accerleratorZ, combinedDelta, tvpotholeCount, tvdistance, tvname;
    ImageView ivProfilePicture;

    private long backPressedTime;
    private double latitude = 0.0, longitude = 0.0;
    private float totalDistance = 0.0f; // Đơn vị: mét
    private Location previousLocation = null; // Lưu vị trí trước đó
    AnyChartView anyChartView;


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

        home_button = findViewById(R.id.home_button);
        maps_button = findViewById(R.id.maps_button);
        history_button = findViewById(R.id.history_button);
        settings_button = findViewById(R.id.settings_button);
        accerleratorX = findViewById(R.id.acceleratorX);
        accerleratorY = findViewById(R.id.acceleratorY);
        accerleratorZ = findViewById(R.id.acceleratorZ);
        combinedDelta = findViewById(R.id.combinedDelta);
        tvdistance = findViewById(R.id.traveledDistance);
        tvpotholeCount = findViewById(R.id.potholeCount);
        ivProfilePicture = findViewById(R.id.user_avatar);
        tvname = findViewById(R.id.user_welcome_message);
        initializeLocation();
        anyChartView = findViewById(R.id.any_chart_view);
        // Đăng ký BroadcastReceiver
        IntentFilter filter = new IntentFilter("com.example.pothole.POTHOLE_DETECTED");
        registerReceiver(potholeReceiver, filter);
        // Đăng ký BroadcastReceiver
        IntentFilter distanceFilter = new IntentFilter("com.example.pothole.TOTAL_DISTANCE_UPDATED");
        registerReceiver(distanceReceiver, distanceFilter);

        IntentFilter potholecountFilter = new IntentFilter("com.example.pothole.POTHOLE_CONFIRMED");
        registerReceiver(potholeConfirmedReceiver, potholecountFilter);
        // Đọc dữ liệu từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("PotholeData", MODE_PRIVATE);
        int savedPotholeCount = prefs.getInt("potholeCount", 0);
        tvpotholeCount.setText(String.format(Locale.getDefault(), "%d", savedPotholeCount));
        loadTotalDistanceFromSharedPreferences();
        tvdistance.setText(String.format(Locale.getDefault(), "%.2f m", totalDistance));
        // Hiển thị ảnh đại diện
        loadProfilePictureAndName();

        // Sự kiện click vào ảnh đại diện để chuyển đến EditProfile
        ivProfilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditProfile.class);
            startActivityForResult(intent, 100); // 100 là requestCode
        });
        tvname.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditProfile.class);
            startActivityForResult(intent, 100); // 100 là requestCode
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

        // Set OnClickListener for AnyChartView
        anyChartView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DailyReportPothole.class);
            startActivity(intent);
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference potholeRef = database.getReference("potholes");
        // Kiểm tra kết nối
        database.getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d("Firebase", "Connected to Firebase");
                } else {
                    Log.d("Firebase", "Disconnected from Firebase");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error checking connection");
            }
        });

        potholeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("Firebase", "onDataChange called");
                if (snapshot.exists()) {
                    Log.d("Firebase", "Data exists: " + snapshot.toString());

                    // Kiểm tra và lấy giá trị một cách an toàn
                    Float deltaX = snapshot.child("deltax").getValue(Float.class);
                    Float deltaY = snapshot.child("deltay").getValue(Float.class);
                    Float deltaZ = snapshot.child("deltaz").getValue(Float.class);
                    Float combinedDeltaValue = snapshot.child("combinedDelta").getValue(Float.class);
                    Integer potholeCount = snapshot.child("potholeCount").getValue(Integer.class);
                    Double totalDistance = snapshot.child("totalDistance").getValue(Double.class);

                    // Đảm bảo giá trị không null trước khi sử dụng
                    accerleratorX.setText(deltaX != null ? String.format(Locale.getDefault(), "%.2f", deltaX) : "N/A");
                    accerleratorY.setText(deltaY != null ? String.format(Locale.getDefault(), "%.2f", deltaY) : "N/A");
                    accerleratorZ.setText(deltaZ != null ? String.format(Locale.getDefault(), "%.2f", deltaZ) : "N/A");
                    combinedDelta.setText(combinedDeltaValue != null ? String.format(Locale.getDefault(), "%.2f", combinedDeltaValue) : "N/A");
                    tvpotholeCount.setText(potholeCount != null ? String.format(Locale.getDefault(), "%d", potholeCount) : "0");
                    tvdistance.setText(totalDistance != null ? String.format(Locale.getDefault(), "%.2f m", totalDistance) : "0.00 m");

                    // Cập nhật dữ liệu vào biểu đồ nếu dữ liệu hợp lệ
                    if (deltaX != null && deltaY != null && deltaZ != null) {
                        List<DataEntry> data = new ArrayList<>();
                        data.add(new ValueDataEntry("Delta X", deltaX));
                        data.add(new ValueDataEntry("Delta Y", deltaY));
                        data.add(new ValueDataEntry("Delta Z", deltaZ));
                        setupPieChart(data);
                    }
                } else {
                    Log.w("Firebase", "No data exists in 'potholeData'");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage(), error.toException());
            }
        });

    }
    private void initializeLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // Tính quãng đường di chuyển
        if (previousLocation != null) {
            float distance = previousLocation.distanceTo(location); // Đơn vị: mét
            totalDistance += distance; // Cộng dồn vào tổng quãng đường
        }
        previousLocation = location;
        // Hiển thị  quãng đường lên giao diện
        tvdistance.setText(String.format(Locale.getDefault(), "Distance: %.2f m", totalDistance));

        Log.d(TAG, "Updated Location: Latitude: " + latitude + ", Longitude: " + longitude +
                ",  Distance: " + totalDistance + " m");
    }

    private void setupPieChart(List<DataEntry> data) {
        Pie pie = AnyChart.pie();
        pie.data(data);
        anyChartView.setChart(pie);
    }

    private BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("com.example.pothole.TOTAL_DISTANCE_UPDATED")) {
                double totalDistance = intent.getDoubleExtra("totalDistance", 0.0);

                // Cập nhật giao diện
                TextView distanceTextView = findViewById(R.id.traveledDistance);
                distanceTextView.setText(String.format(Locale.getDefault(), "Total Distance: %.2f meters", totalDistance));
            }
        }
    };

    private final BroadcastReceiver potholeConfirmedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update the pothole count and UI
            SharedPreferences prefs = getSharedPreferences("PotholeData", MODE_PRIVATE);
            int savedPotholeCount = prefs.getInt("potholeCount", 0);
            tvpotholeCount.setText(String.format(Locale.getDefault(), "%d", savedPotholeCount));

            // Restart MainActivity
            Intent restartIntent = new Intent(context, MainActivity.class);
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(restartIntent);
            finish(); // Ensure the current activity is finished
        }
    };
    private final BroadcastReceiver potholeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("com.example.pothole.POTHOLE_DETECTED")) {
                float deltaX = intent.getFloatExtra("deltaX", 0);
                float deltaY = intent.getFloatExtra("deltaY", 0);
                float deltaZ = intent.getFloatExtra("deltaZ", 0);
                float combinedDelta = intent.getFloatExtra("combinedDelta", 0);
                double totalDistance = intent.getDoubleExtra("totalDistance", 0);

                accerleratorX.setText(String.format(Locale.getDefault(), "%.2f", deltaX));
                accerleratorY.setText(String.format(Locale.getDefault(), "%.2f", deltaY));
                accerleratorZ.setText(String.format(Locale.getDefault(), "%.2f", deltaZ));
                MainActivity.this.combinedDelta.setText(String.format(Locale.getDefault(), "%.2f", combinedDelta));
                tvdistance.setText(String.format(Locale.getDefault(), "%.2f m", totalDistance));

                // Cập nhật dữ liệu vào biểu đồ
                List<DataEntry> data = new ArrayList<>();
                data.add(new ValueDataEntry("Delta X", deltaX));
                data.add(new ValueDataEntry("Delta Y", deltaY));
                data.add(new ValueDataEntry("Delta Z", deltaZ));
                setupPieChart(data);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.example.pothole.TOTAL_DISTANCE_UPDATED");
        registerReceiver(distanceReceiver, filter);

        IntentFilter potholecountFilter = new IntentFilter("com.example.pothole.POTHOLE_CONFIRMED");
        registerReceiver(potholeConfirmedReceiver, potholecountFilter);


        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String profileImageBase64 = prefs.getString("profileImage", null);
        if (profileImageBase64 != null) {
            Bitmap bitmap = base64ToBitmap(profileImageBase64);
            ivProfilePicture.setImageBitmap(bitmap);
        }
    }

    private void loadProfilePictureAndName() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String base64Image = sharedPreferences.getString("profilePicture", null);

        String name = sharedPreferences.getString("name", "");
        tvname.setText("Welcome back, " + name);
        if (base64Image != null) {
            Bitmap bitmap = base64ToBitmap(base64Image);
            ivProfilePicture.setImageBitmap(bitmap);
        }
    }

    private Bitmap base64ToBitmap(String base64String) {
        byte[] decodedBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void saveTotalDistanceToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("PotholeData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("totalDistance", totalDistance);
        editor.apply();
    }
    private void loadTotalDistanceFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("PotholeData", MODE_PRIVATE);
        totalDistance = sharedPreferences.getFloat("totalDistance", 0.0f);
    }
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(potholeReceiver);
        unregisterReceiver(distanceReceiver);

    
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveTotalDistanceToSharedPreferences();
        unregisterReceiver(potholeConfirmedReceiver);
    }
}