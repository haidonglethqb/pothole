package com.example.pothole.SettingScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pothole.R;
import com.example.pothole.Other.TestActivity;

public class Notificatons_settings extends AppCompatActivity {
    ImageView ivBack;
    Button test;
    private Switch notificationSwitch;
    private Spinner soundSpinner;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificatons_settings);

        // Ánh xạ view
        ivBack = findViewById(R.id.ivBack);
        test = findViewById(R.id.bttest);
        notificationSwitch = findViewById(R.id.switchNotifications);
        soundSpinner = findViewById(R.id.soundSpinner);

        // Quay lại màn hình trước
        ivBack.setOnClickListener(v -> finish());

        // Chuyển đến màn hình test thông báo
        test.setOnClickListener(v -> {
            Intent intent = new Intent(Notificatons_settings.this, TestActivity.class);
            startActivity(intent);
        });

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);

        // Load trạng thái thông báo từ SharedPreferences
        boolean isNotificationEnabled = sharedPreferences.getBoolean("notificationsEnabled", true);
        notificationSwitch.setChecked(isNotificationEnabled);

        // Cập nhật trạng thái Toggle
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationsEnabled", isChecked);
            editor.apply();

            String message = isChecked ? "Thông báo đã bật" : "Thông báo đã tắt";
            Toast.makeText(Notificatons_settings.this, message, Toast.LENGTH_SHORT).show();
        });

        // Danh sách âm thanh
        String[] soundOptions = {"Âm thanh 1", "Âm thanh 2"};
        int[] soundResources = {R.raw.sound4, R.raw.sound5};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, soundOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        soundSpinner.setAdapter(adapter);

        // Load âm thanh đã chọn từ SharedPreferences
        String selectedSound = sharedPreferences.getString("selectedSound", "Âm thanh 1");
        int soundPosition = adapter.getPosition(selectedSound);
        soundSpinner.setSelection(soundPosition);

        // Tạo MediaPlayer để phát âm thanh khi chọn
        final MediaPlayer[] mediaPlayer = {new MediaPlayer()};

        soundSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedSound = soundOptions[position];
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selectedSound", selectedSound);
                editor.putInt("selectedSoundResource", soundResources[position]); // Lưu âm thanh đã chọn
                editor.apply();

                // Đảm bảo MediaPlayer được xử lý đúng trong Spinner
                if (mediaPlayer[0] != null) {
                    mediaPlayer[0].stop();
                    mediaPlayer[0].release();
                }
                mediaPlayer[0] = MediaPlayer.create(Notificatons_settings.this, soundResources[position]);
                mediaPlayer[0].start();

                Toast.makeText(Notificatons_settings.this, "Âm thanh đã chọn: " + selectedSound, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Không làm gì
            }
        });
    }

    // Hàm tiện ích để lấy âm thanh thông báo
    public static int getNotificationSoundResource(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("selectedSoundResource", R.raw.sound4); // Mặc định là sound1
    }
}
