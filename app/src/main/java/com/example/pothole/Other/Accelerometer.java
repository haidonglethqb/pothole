package com.example.pothole.Other;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.pothole.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.location.LocationListener;

public class Accelerometer extends Activity implements SensorEventListener , LocationListener {

    private static final String TAG = "AccelerometerApp";

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float lastX, lastY, lastZ;
    private float deltaX, deltaY, deltaZ;
    private String severity;

    private TextView sensorName, sensorType, maxRange, resolution;
    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, latitudeText, longitudeText, speed, distance;
    private TextView potholeTotal, minorPothole, mediumPothole, severePothole, combinedDelta;

    private float deltaXMax = 0, deltaYMax = 0, deltaZMax = 0;

    private int potholeCount = 0;
    private int minorCount = 0;
    private int mediumCount = 0;
    private int severeCount = 0;
    private LocationManager locationManager;
    private long lastDetectionTime = 0;
    private static final long DETECTION_THRESHOLD_MS = 1000;

    private Vibrator v;

    private DatabaseReference databaseReference, database;

    private double latitude = 0.0, longitude = 0.0;
    private float totalDistance = 0.0f; // Đơn vị: mét
    private Location previousLocation = null; // Lưu vị trí trước đó
    Uri selectedRingtoneUri; // Khai báo biến

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        initializeViews();
        initializeSensor();
        initializeLocation();

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pothole-060104-default-rtdb.firebaseio.com/");
        loadDataFromFirebase();


        // Retrieve the saved ringtone URI from SharedPreferences
        String ringtoneUriString = getSharedPreferences("PotholeSettings", MODE_PRIVATE)
                .getString("notification_ringtone", null);
        if (ringtoneUriString != null) {
            selectedRingtoneUri = Uri.parse(ringtoneUriString);
        }
    }

    private void initializeViews() {
        sensorName = findViewById(R.id.sensorName);
        sensorType = findViewById(R.id.sensorType);
        maxRange = findViewById(R.id.maxRange);
        resolution = findViewById(R.id.resolution);
        currentX = findViewById(R.id.currentX);
        currentY = findViewById(R.id.currentY);
        currentZ = findViewById(R.id.currentZ);

        maxX = findViewById(R.id.maxX);
        maxY = findViewById(R.id.maxY);
        maxZ = findViewById(R.id.maxZ);

        latitudeText = findViewById(R.id.latitude);
        longitudeText = findViewById(R.id.longitude);
        speed = findViewById(R.id.Speed);
        distance = findViewById(R.id.Distance);

        potholeTotal = findViewById(R.id.potholeTotal);
        minorPothole = findViewById(R.id.minorPothole);
        mediumPothole = findViewById(R.id.mediumPothole);
        severePothole = findViewById(R.id.severePothole);
        combinedDelta = findViewById(R.id.combinedDelta);
        Button butReset = findViewById(R.id.buttonReset);
        butReset.setOnClickListener(view -> resetData());
    }

    private void initializeSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            if (accelerometer != null) {
                sensorName.setText("Sensor Name: " + accelerometer.getName());
                sensorType.setText("Sensor Type: " + accelerometer.getStringType());
                maxRange.setText("Max Range: " + accelerometer.getMaximumRange());
                resolution.setText("Resolution: " + accelerometer.getResolution());
            }
        } else {
            Log.e(TAG, "Accelerometer not available.");
        }
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
        float speedValue = location.getSpeed(); // Tốc độ theo m/s
        float speedKmH = speedValue * 3.6f;     // Chuyển đổi sang km/h

        // Tính quãng đường di chuyển
        if (previousLocation != null) {
            float distance = previousLocation.distanceTo(location); // Đơn vị: mét
            totalDistance += distance; // Cộng dồn vào tổng quãng đường
        }
        previousLocation = location;

        // Hiển thị tốc độ và quãng đường lên giao diện
        speed.setText(String.format(Locale.getDefault(), "Speed: %.2f km/h", speedKmH));
        distance.setText(String.format(Locale.getDefault(), "Distance: %.2f m", totalDistance));

        Log.d(TAG, "Updated Location: Latitude: " + latitude + ", Longitude: " + longitude +
                ", Speed: " + speedKmH + " km/h, Distance: " + totalDistance + " m");
    }


    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}


    @Override
    public void onSensorChanged(SensorEvent event) {
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        displayCurrentValues();
        displayMaxValues();
        detectPothole();
    }

    private void playNotificationSound() {
        try {
            Ringtone ringtone;

            if (selectedRingtoneUri != null) {
                ringtone = RingtoneManager.getRingtone(this, selectedRingtoneUri);
            } else {
                ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }

            if (ringtone != null && !ringtone.isPlaying()) {
                ringtone.play();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing ringtone", e);
        }
    }
    private void detectPothole() {
        float combinedDelta = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        if (combinedDelta > 15) {
            long currentTime = System.currentTimeMillis();

            // Kiểm tra khoảng thời gian phát hiện và tốc độ
            if (currentTime - lastDetectionTime >= DETECTION_THRESHOLD_MS && previousLocation != null) {
                float speedValue = previousLocation.getSpeed(); // Tốc độ theo m/s
                float speedKmH = speedValue * 3.6f; // Chuyển đổi sang km/h

                if (speedKmH >= 10) { // Điều kiện tốc độ tối thiểu 5 km/h
                    lastDetectionTime = currentTime;

                    // Phân loại mức độ ổ gà
                    if (combinedDelta < 20) {
                        severity = "Minor";
                    } else if (combinedDelta < 25) {
                        severity = "Medium";
                    } else {
                        severity = "Severe";
                    }

                    // Gọi phương thức để lưu dữ liệu và cập nhật giao diện
                    initializeLocation();
                    saveToFirebase(severity, deltaX, deltaY, deltaZ, combinedDelta, latitude, longitude);
                    // Play notification sound
                    playNotificationSound();
                    saveCountsToSharedPreferences();
                    showConfirmDialog(severity, deltaX, deltaY, deltaZ, combinedDelta, latitude, longitude);
                    updateUI();
                } else {
                    Log.d(TAG, "Speed too low: " + speedKmH + " km/h. Skipping pothole detection.");
                }
            }
        }
    }

//



    private void saveToFirebase(String severity, float deltaX, float deltaY, float deltaZ, float combinedDelta, double latitude, double longitude) {
        // Check if database reference is initialized
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        // Generate a unique key for each pothole entry
        String potholeId = databaseReference.child("potholes").push().getKey();

        if (potholeId == null) {
            Log.e(TAG, "Failed to generate unique key for pothole");
            return;
        }

        // Create pothole data object
        PotholeData potholeData = new PotholeData(
                potholeCount,
                severity,
                deltaX,
                deltaY,
                deltaZ,
                combinedDelta,
                latitude,
                longitude,
                System.currentTimeMillis(),
                getCurrentDateTime()
        );

        // Save data with error handling and completion listener
        databaseReference.child("potholes").child(potholeId).setValue(potholeData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Pothole data saved successfully. ID: " + potholeId);
                    // Optional: Show a toast to user
                    runOnUiThread(() -> Toast.makeText(this, "Pothole saved ", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save pothole data", e);
                    // Optional: Show error toast
                    runOnUiThread(() -> Toast.makeText(this, "Failed to record pothole", Toast.LENGTH_SHORT).show());
                });
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }


    private void updateUI() {
        potholeTotal.setText("Potholes: " + potholeCount);
        minorPothole.setText("Minor: " + minorCount);
        mediumPothole.setText("Medium: " + mediumCount);
        severePothole.setText("Severe: " + severeCount);
        combinedDelta.setText("Combined Delta: " + (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
        latitudeText.setText("Latitude: " + latitude);
        longitudeText.setText("Longitude: " + longitude);
        speed.setText("Speed: 0.0 km/h");
    }

    private void displayCurrentValues() {
        currentX.setText("Current X: " + deltaX);
        currentY.setText("Current Y: " + deltaY);
        currentZ.setText("Current Z: " + deltaZ);
    }

    private void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText("Max X: " + deltaXMax);
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText("Max Y: " + deltaYMax);
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText("Max Z: " + deltaZMax);
        }
    }

    private void resetData() {
        // Đặt lại giá trị gia tốc và số liệu pothole
        deltaXMax = deltaYMax = deltaZMax = 0;
        potholeCount = minorCount = mediumCount = severeCount = 0;

        // Đặt lại tọa độ (nếu cần)
        latitude = 0.0;
        longitude = 0.0;
        totalDistance = 0.0f;

        // Cập nhật giao diện
        displayReset();
        updateUI();
    }

    private void displayReset() {
        currentX.setText("Current X: 0.0");
        currentY.setText("Current Y: 0.0");
        currentZ.setText("Current Z: 0.0");

        maxX.setText("Max X: 0.0");
        maxY.setText("Max Y: 0.0");
        maxZ.setText("Max Z: 0.0");

        potholeTotal.setText("Potholes: 0");
        minorPothole.setText("Minor: 0");
        mediumPothole.setText("Medium: 0");
        severePothole.setText("Severe: 0");
        combinedDelta.setText("Combined Delta: 0.0");
        distance.setText("Distance: 0.0 m");
    }


    private void saveCountsToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("PotholeData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("potholeCount", potholeCount);
        editor.putInt("minorCount", minorCount);
        editor.putInt("mediumCount", mediumCount);
        editor.putInt("severeCount", severeCount);
        editor.putFloat("totalDistance", totalDistance);
        editor.apply();
    }
    private void loadCountsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("PotholeData", MODE_PRIVATE);
        potholeCount = sharedPreferences.getInt("potholeCount", 0);  // 0 là giá trị mặc định nếu không tìm thấy
        minorCount = sharedPreferences.getInt("minorCount", 0);
        mediumCount = sharedPreferences.getInt("mediumCount", 0);
        severeCount = sharedPreferences.getInt("severeCount", 0);
        totalDistance = sharedPreferences.getFloat("totalDistance", 0.0f);

        updateUI();  // Cập nhật UI với các giá trị đã lưu
    }
    private void showConfirmDialog(final String severity, final float deltaX, final float deltaY, final float deltaZ, final float combinedDelta,final double latitude, final double longitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Pothole Detection")
                .setMessage("Do you want to save the pothole data?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        initializeLocation();
                        // Nếu người dùng xác nhận, lưu dữ liệu
                        // Cộng thêm các chỉ số nếu lưu thành công
                        saveToFirebase(severity, deltaX, deltaY, deltaZ,combinedDelta ,latitude ,longitude);

                        potholeCount++;
                        if ("Minor".equals(severity)) {
                            minorCount++;
                        } else if ("Medium".equals(severity)) {
                            mediumCount++;
                        } else if ("Severe".equals(severity)) {
                            severeCount++;
                        }

                        // Lưu lại vào SharedPreferences
                        saveCountsToSharedPreferences();

                        // Cập nhật giao diện người dùng
                        updateUI();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn No, không làm gì và không thay đổi bất kỳ chỉ số nào
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }

    private void loadDataFromFirebase() {
        databaseReference.child("potholes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot potholeSnapshot : snapshot.getChildren()) {
                        // Duyệt qua từng mục pothole
                        String key = potholeSnapshot.getKey();
                        PotholeData data = potholeSnapshot.getValue(PotholeData.class);

                        if (data != null) {
                            Log.d("PotholeData", "Pothole: " + key + " | " + data.toString());
                            // Bạn có thể cập nhật giao diện hoặc biến đếm ở đây
                        }
                    }
                } else {
                    Log.w("Firebase", "No data found in Firebase.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error reading data: " + error.getMessage());
            }
        });
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromFirebase();
        loadCountsFromSharedPreferences();// Tải lại dữ liệu từ SharedPreferences


        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    protected void onStop() {
        super.onStop();

        // Lưu dữ liệu vào SharedPreferences
        saveCountsToSharedPreferences();


        ;
    }


}
