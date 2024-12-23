package com.example.pothole.SettingScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pothole.Authentication.Forgetpassword;
import com.example.pothole.Authentication.register;
import com.example.pothole.DashboardScreen.MainActivity;
import com.example.pothole.MapScreen.mapdisplay;
import com.example.pothole.Other.Accelerometer;
import com.example.pothole.Authentication.login;
import com.example.pothole.Other.History;
import com.example.pothole.R;

public class Settings extends BaseActivity {
    TextView tvEdit, tvLanguage, tvNotify, tvlogout, tvReport,tvSound,tvAddaccount, tvFreeupspace,tvSupport, tvResetPassword;
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
        tvlogout = findViewById(R.id.tvLogOut);
        tvReport = findViewById(R.id.tvReportProblem);
        tvSound = findViewById(R.id.tvAlertSound);
        back_button = findViewById(R.id.btnBack);
        tvAddaccount = findViewById(R.id.tvAddAccount);
        tvFreeupspace = findViewById(R.id.tvFreeUpSpace);
        tvSupport = findViewById(R.id.tvSupport);
        tvResetPassword = findViewById(R.id.tvResetPassword);

        back_button.setOnClickListener(v -> finish());

        tvEdit.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, EditProfile.class);
            startActivity(intent);
        });

        tvLanguage.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, ChooseLanguge.class);
            startActivity(intent);
        });
        tvlogout.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, login.class);
            startActivity(intent);
            finish();
        });
        tvReport.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, Accelerometer.class);
            startActivity(intent);
        });

        tvSound.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, AlertSound_Setting.class);
            startActivity(intent);
        });

        tvAddaccount.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, register.class);
            startActivity(intent);
            finish();
        });

        tvFreeupspace.setOnClickListener(view -> {
            // Display the first Toast message
            Toast.makeText(Settings.this, "Clearing cache...", Toast.LENGTH_SHORT).show();

            // Use a Handler to display the second Toast message after 3 seconds
            new Handler().postDelayed(() -> {
                Toast.makeText(Settings.this, "Cache cleared", Toast.LENGTH_SHORT).show();
            }, 3000); // 3000 milliseconds = 3 seconds
        });

        tvResetPassword.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, Forgetpassword.class);
            startActivity(intent);
        });

        tvSupport.setOnClickListener(view -> {
            String url = "https://drive.google.com/file/d/1zzHkqB4rUpn8qhXwbS-u3bPxpc_iF54Y/view?usp=sharing";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
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
            finish();
        });

        maps_button.setOnClickListener(view -> {
            // Handle maps button click
            Intent intent = new Intent(Settings.this, mapdisplay.class);
            startActivity(intent);
            finish();
        });

        history_button.setOnClickListener(view -> {
            // Handle history button click
            Intent intent = new Intent(Settings.this, History.class);
            startActivity(intent);
            finish();
        });

        settings_button.setOnClickListener(view -> {
            // Handle settings button click
            Toast.makeText(Settings.this, "You are already on the settings screen", Toast.LENGTH_SHORT).show();
        });


    }
}