package com.example.pothole.SettingScreen;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import com.example.pothole.R;

public class AlertSound_Setting extends AppCompatActivity {

    private static final int REQUEST_CODE_RINGTONE = 1;
    private TextView selectedRingtoneTextView;
    private Button selectRingtoneButton;
    private ImageButton btnBack;
    private Uri selectedRingtoneUri;
    private ImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alert_sound_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Select ringtone
        selectedRingtoneTextView = findViewById(R.id.selected_ringtone);
        selectRingtoneButton = findViewById(R.id.bt_select_ringtone);

        //
        selectRingtoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Ringtone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtoneUri);
            startActivityForResult(intent, REQUEST_CODE_RINGTONE);
        });

        // Load the saved ringtone URI from SharedPreferences
        String ringtoneUriString = getSharedPreferences("PotholeSettings", MODE_PRIVATE)
                .getString("notification_ringtone", null);
        if (ringtoneUriString != null) {
            selectedRingtoneUri = Uri.parse(ringtoneUriString);
            selectedRingtoneTextView.setText("Ringtone selected:" +RingtoneManager.getRingtone(this, selectedRingtoneUri).getTitle(this));
        }

        // Load the gif image
        // Load the gif image
        gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this).asGif().load(R.drawable.sound).into(gifImageView);

        // Back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // Xử lý kết quả của hoạt động chọn nhạc chuông
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RINGTONE && resultCode == RESULT_OK) {
            selectedRingtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (selectedRingtoneUri != null) {
                selectedRingtoneTextView.setText("Ringtone selected: " + RingtoneManager.getRingtone(this, selectedRingtoneUri).getTitle(this));

                // Lưu URI nhạc chuông đã chọn vào SharedPreferences
                getSharedPreferences("PotholeSettings", MODE_PRIVATE).edit()
                        .putString("notification_ringtone", selectedRingtoneUri.toString())
                        .apply();
            }
        }
    }
}