package com.example.pothole.SettingScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pothole.DashboardScreen.MainActivity;
import com.example.pothole.MapScreen.mapdisplay;
import com.example.pothole.Other.Accelerometer;
import com.example.pothole.Authentication.login;
import com.example.pothole.Other.History;
import com.example.pothole.R;

public class Settings extends BaseActivity {
    TextView tvEdit, tvLanguage, tvNotify, tvlogout, tvReport;
    ImageButton back_button, home_button, maps_button, history_button, settings_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvEdit = findViewById(R.id.tvEditProfile);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvNotify = findViewById(R.id.tvNotifications);
        tvlogout = findViewById(R.id.tvLogOut);
        tvReport = findViewById(R.id.tvReportProblem);
        back_button = findViewById(R.id.btnBack);

        back_button.setOnClickListener(v -> finish());

        tvEdit.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, EditProfile.class);
            startActivity(intent);
        });
        tvNotify.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, Notificatons_settings.class);
            startActivity(intent);
        });
        tvLanguage.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, ChooseLanguge.class);
            startActivity(intent);
        });
        tvlogout.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, login.class);
            startActivity(intent);
        });
        tvReport.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, Accelerometer.class);
            startActivity(intent);
        });

        // Find navigation bar buttons
        home_button = findViewById(R.id.home_button);
        maps_button = findViewById(R.id.maps_button);
        history_button = findViewById(R.id.history_button);
        settings_button = findViewById(R.id.settings_button);

        // Set click listeners for navigation bar buttons
        home_button.setOnClickListener(view -> {
            // Handle home button click
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
        });

        maps_button.setOnClickListener(view -> {
            // Handle maps button click
            Intent intent = new Intent(Settings.this, mapdisplay.class);
            startActivity(intent);
        });

        history_button.setOnClickListener(view -> {
            // Handle history button click
            Intent intent = new Intent(Settings.this, History.class);
            startActivity(intent);
        });

        settings_button.setOnClickListener(view -> {
            // Handle settings button click
            Toast.makeText(Settings.this, "You are already on the settings screen", Toast.LENGTH_SHORT).show();
        });
    }
}