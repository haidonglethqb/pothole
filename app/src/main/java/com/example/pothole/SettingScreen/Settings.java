package com.example.pothole.SettingScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pothole.Other.Accelerometer;
import com.example.pothole.Authentication.login;
import com.example.pothole.R;

public class Settings extends BaseActivity {
    TextView tvEdit, tvLanguage, tvNotify,tvlogout, tvReport ;

    ImageButton back_button;
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
        back_button.setOnClickListener(v -> {
            finish();
        });

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, EditProfile.class);
                startActivity(intent);
            }
        });
        tvNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, Notificatons_settings.class);
                startActivity(intent);
            }
        });
        tvLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, ChooseLanguge.class);
                startActivity(intent);
            }
        });
        tvlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, login.class);
                startActivity(intent);
            }
        });
        tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, Accelerometer.class);
                startActivity(intent);
            }
        });


    }


}