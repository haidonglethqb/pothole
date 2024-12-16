package com.example.pothole.SettingScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pothole.R;

public class Notificatons_settings extends AppCompatActivity {
    private ImageView ivBack;
    private Switch notificationSwitch;
    private Spinner modeSpinner;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificatons_settings);

        // Ánh xạ view
        ivBack = findViewById(R.id.ivBack);
        notificationSwitch = findViewById(R.id.switchNotifications);
        modeSpinner = findViewById(R.id.modeSpinner);

        // Quay lại màn hình trước
        ivBack.setOnClickListener(v -> finish());

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Thiết lập dữ liệu cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.notification_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(adapter);

        // Đặt trạng thái mặc định cho Switch và Spinner
        boolean isNotificationOn = sharedPreferences.getBoolean("notifications_enabled", true);
        notificationSwitch.setChecked(isNotificationOn);

        String mode = sharedPreferences.getString("notification_mode", "Sound");
        int spinnerPosition = adapter.getPosition(mode);
        modeSpinner.setSelection(spinnerPosition);

        // Lắng nghe sự thay đổi của Switch
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("notifications_enabled", isChecked);
            editor.apply();
            Toast.makeText(this, "Notifications " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        // Lắng nghe sự thay đổi chế độ trong Spinner
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMode = parent.getItemAtPosition(position).toString();
                editor.putString("notification_mode", selectedMode);
                editor.apply();

                if ("Vibrate".equals(selectedMode)) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null && vibrator.hasVibrator()) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            // API 26 trở lên
                            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            // API thấp hơn 26
                            vibrator.vibrate(500); // Rung trong 500ms
                        }
                        Toast.makeText(Notificatons_settings.this, "Vibrate Mode Activated", Toast.LENGTH_SHORT).show();
                    }
                }
                // Hiển thị thông báo tương ứng với chế độ được chọn
                else if ("Silent".equals(selectedMode)) {
                    Toast.makeText(Notificatons_settings.this, "Silent Mode Activated", Toast.LENGTH_SHORT).show();
                } else if ("Sound".equals(selectedMode)) {
                    Toast.makeText(Notificatons_settings.this, "Sound Mode Activated", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
}


