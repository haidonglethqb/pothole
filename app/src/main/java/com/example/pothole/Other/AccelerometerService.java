package com.example.pothole.Other;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccelerometerService extends Service implements SensorEventListener, LocationListener {

    private static final String TAG = "AccelerometerService";

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private LocationManager locationManager;
    private Vibrator vibrator;

    private float lastX, lastY, lastZ;
    private float deltaX, deltaY, deltaZ;
    private double latitude, longitude,speed;
    private int potholeCount = 0;
    private int minorCount = 0;
    private int mediumCount = 0;
    private int severeCount = 0;
    private DatabaseReference databaseReference;
    private String severity;

    private long lastDetectionTime = 0;
    private static final long DETECTION_THRESHOLD_MS = 1000;

    private double totalDistance = 0.0; // Tổng quãng đường
    private Location lastLocation = null;// Vị trí lần cuối
    private float speedKmh = 0.0f; // Tốc độ km/h

    Uri selectedRingtoneUri; // Khai báo biến
    private Location lastPotholeLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }


        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pothole-060104-default-rtdb.firebaseio.com/");
        SharedPreferences prefs = getSharedPreferences("PotholeData", MODE_PRIVATE);

        // Khởi tạo giá trị từ SharedPreferences
        potholeCount = prefs.getInt("potholeCount", 0);
        totalDistance = prefs.getFloat("totalDistance", 0.0f);

        IntentFilter filter = new IntentFilter("com.example.pothole.POTHOLE_CONFIRMED");
        registerReceiver(potholeConfirmedReceiver, filter);

        // Retrieve the saved ringtone URI from SharedPreferences
        String ringtoneUriString = getSharedPreferences("PotholeSettings", MODE_PRIVATE)
                .getString("notification_ringtone", null);
        if (ringtoneUriString != null) {
            selectedRingtoneUri = Uri.parse(ringtoneUriString);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        unregisterReceiver(potholeConfirmedReceiver);
    }
    public final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public AccelerometerService getService() {
            return AccelerometerService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        detectPothole();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        speed = location.getSpeed(); // Tính bằng m/s
        double speedKmH = speed * 3.6f; // Chuyển sang km/h

        if (lastLocation != null) {
            totalDistance += lastLocation.distanceTo(location);
        }
        Intent intent = new Intent("com.example.pothole.LOCATION_UPDATE");
        intent.putExtra("totalDistance", totalDistance);

        // Cập nhật lastLocation
        lastLocation = location;

        Log.d(TAG, "Updated Location: Latitude: " + latitude + ", Longitude: " + longitude + ", Speed: " + speed);
    }


    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

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

            if (currentTime - lastDetectionTime >= DETECTION_THRESHOLD_MS) {
                initializeLocation();

                Location currentLocation = new Location("currentLocation");
                currentLocation.setLatitude(latitude);
                currentLocation.setLongitude(longitude);

                // Kiểm tra bán kính 10m
                if (lastPotholeLocation != null) {
                    float distanceToLastPothole = lastPotholeLocation.distanceTo(currentLocation);
                    if (distanceToLastPothole <= 10) {
                        Log.d(TAG, "Pothole ignored as it's within 10m radius: " + distanceToLastPothole + " meters.");
                        return; // Bỏ qua nếu trong bán kính 10m
                    }
                }

                lastDetectionTime = currentTime;

                // Đánh giá mức độ severity
                if(combinedDelta >20){

                if (combinedDelta < 25) {
                    severity = "Minor";
                } else if (combinedDelta < 30) {
                    severity = "Medium";
                } else {
                    severity = "Severe";
                }
                }


                // Cập nhật vị trí ổ gà cuối cùng
                lastPotholeLocation = currentLocation;

                // Tăng số lượng ổ gà và cập nhật khoảng cách
                //potholeCount++;
                // Lưu vào SharedPreferences

                saveToPreferences("totalDistance", (float) totalDistance);

                // Lưu dữ liệu vào Firebase
                //saveToFirebase(potholeCount, severity, deltaX, deltaY, deltaZ, combinedDelta, latitude, longitude);

                // Phát âm thanh thông báo
                playNotificationSound();

                // Gửi broadcast
                Intent intent = new Intent("com.example.pothole.POTHOLE_DETECTED");
                intent.putExtra("severity", severity);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("deltaX", deltaX);
                intent.putExtra("deltaY", deltaY);
                intent.putExtra("deltaZ", deltaZ);
                intent.putExtra("combinedDelta", combinedDelta);
                intent.putExtra("timestamp", System.currentTimeMillis());
                intent.putExtra("dateTime", getCurrentDateTime());


                sendBroadcast(intent);
            }
        }
    }

    private final BroadcastReceiver potholeConfirmedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.pothole.POTHOLE_CONFIRMED".equals(intent.getAction())) {
                potholeCount++;
                saveToPreferences("potholeCount", potholeCount);
            }
        }
    };


    private void initializeLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    private void saveToFirebase(int potholeCount,String severity, float deltaX, float deltaY, float deltaZ, float combinedDelta, double latitude, double longitude) {
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
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Pothole data saved successfully. ID: " + potholeId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save pothole data", e));
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
                        saveToFirebase(potholeCount, severity, deltaX, deltaY, deltaZ, combinedDelta, latitude, longitude);

                        potholeCount++;
                        if ("Minor".equals(severity)) {
                            minorCount++;
                        } else if ("Medium".equals(severity)) {
                            mediumCount++;
                        } else if ("Severe".equals(severity)) {
                            severeCount++;
                        }

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
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }


    // Hàm lưu dữ liệu
    private void saveToPreferences(String key, float value) {
        getSharedPreferences("PotholeData", MODE_PRIVATE)
                .edit()
                .putFloat(key, value)
                .apply();
    }

    private void saveToPreferences(String key, int value) {
        getSharedPreferences("PotholeData", MODE_PRIVATE)
                .edit()
                .putInt(key, value)
                .apply();
    }

    // Hàm đọc dữ liệu
    private float getFloatFromPreferences(String key, float defaultValue) {
        return getSharedPreferences("PotholeData", MODE_PRIVATE)
                .getFloat(key, defaultValue);
    }

    private int getIntFromPreferences(String key, int defaultValue) {
        return getSharedPreferences("PotholeData", MODE_PRIVATE)
                .getInt(key, defaultValue);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


}
