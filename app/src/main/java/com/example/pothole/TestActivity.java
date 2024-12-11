package com.example.pothole;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button testNotificationButton = findViewById(R.id.testNotificationButton);
        Button backToSettingsButton = findViewById(R.id.backToSettingsButton);

        // Nút Test thông báo
        testNotificationButton.setOnClickListener(v -> sendTestNotification());

        // Nút Quay lại Settings
        backToSettingsButton.setOnClickListener(v -> finish()); // Quay lại màn hình trước
    }

    // Hàm gửi thông báo giả lập
    private void sendTestNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", true);
        int selectedSoundResource = sharedPreferences.getInt("selectedSoundResource", R.raw.sound4);

        if (!notificationsEnabled) {
            Toast.makeText(this, "Thông báo đang bị tắt", Toast.LENGTH_SHORT).show();
            return;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + selectedSoundResource);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Xóa kênh cũ nếu đã tồn tại
            NotificationChannel existingChannel = notificationManager.getNotificationChannel("test_channel");
            if (existingChannel != null) {
                notificationManager.deleteNotificationChannel("test_channel");
            }

            // Tạo kênh mới với âm thanh tùy chỉnh
            NotificationChannel channel = new NotificationChannel(
                    "test_channel",
                    "Test Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setSound(soundUri, null);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "test_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Test Thông báo")
                .setContentText("Đây là thông báo để kiểm tra các tùy chỉnh.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(soundUri)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
