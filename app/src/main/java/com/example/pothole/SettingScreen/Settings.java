package com.example.pothole.SettingScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

import java.io.File;

public class Settings extends BaseActivity {
    private static final String TAG = "Settings"; // Add this line

    TextView tvEdit, tvLanguage, tvNotify, tvlogout, tvReport, tvSound, tvAddaccount, tvFreeupspace, tvSupport, tvResetPassword;
    ImageButton back_button, home_button, maps_button, history_button, settings_button;
    RelativeLayout edit_profile_layout, language_layout, notifications_layout, alert_sound_layout, add_account_layout, log_out_layout;
    RelativeLayout free_up_space_layout, data_usage_layout, report_problem_layout;

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

        edit_profile_layout = findViewById(R.id.edit_profile_layout);
        language_layout = findViewById(R.id.language_layout);
        notifications_layout = findViewById(R.id.notifications_layout);
        alert_sound_layout = findViewById(R.id.alert_sound_layout);
        free_up_space_layout = findViewById(R.id.free_up_space_layout);
        data_usage_layout = findViewById(R.id.data_saver_layout);
        add_account_layout = findViewById(R.id.add_account_layout);
        log_out_layout = findViewById(R.id.log_out_layout);
        report_problem_layout = findViewById(R.id.report_problem_layout); // Ensure this ID matches your XML

        back_button.setOnClickListener(v -> finish());

        edit_profile_layout.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, EditProfile.class);
            startActivity(intent);
        });

        language_layout.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, ChooseLanguge.class);
            startActivity(intent);
        });

        notifications_layout.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, login.class);
            startActivity(intent);
            finish();
        });

        report_problem_layout.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, Accelerometer.class);
            startActivity(intent);
        });

        alert_sound_layout.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, AlertSound_Setting.class);
            startActivity(intent);
        });

        add_account_layout.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, register.class);
            startActivity(intent);
            finish();
        });

        log_out_layout.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, login.class);
            startActivity(intent);
            finish();
        });

        free_up_space_layout.setOnClickListener(view -> {
            clearAppCache();
        });

        data_usage_layout.setOnClickListener(view -> {
            String url = "https://drive.google.com/file/d/1zzHkqB4rUpn8qhXwbS-u3bPxpc_iF54Y/view?usp=sharing";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        tvResetPassword.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, Forgetpassword.class);
            startActivity(intent);
        });

        home_button = findViewById(R.id.home_button);
        maps_button = findViewById(R.id.maps_button);
        history_button = findViewById(R.id.history_button);
        settings_button = findViewById(R.id.settings_button);

        home_button.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        maps_button.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, mapdisplay.class);
            startActivity(intent);
            finish();
        });

        history_button.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, History.class);
            startActivity(intent);
            finish();
        });

        settings_button.setOnClickListener(view -> {
            Toast.makeText(Settings.this, "You are already on the settings screen", Toast.LENGTH_SHORT).show();
        });
    }

    private void clearAppCache() {
        try {
            File cacheDir = getCacheDir();
            long sizeBefore = getDirSize(cacheDir);
            deleteDir(cacheDir);
            long sizeAfter = getDirSize(cacheDir);
            long freedSpace = sizeBefore - sizeAfter;

            Toast.makeText(Settings.this,
                    "Freed " + formatSize(freedSpace) + " of cache",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(Settings.this, "Unable to clear cache", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error clearing cache", e);
        }

    }

    private long getDirSize(File dir) {
        long size = 0;
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                size += getDirSize(file);
            }
        } else {
            size = dir.length();
        }
        return size;
    }

    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[] { "B", "KB", "MB", "GB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.1f %s",
                size / Math.pow(1024, digitGroups),
                units[digitGroups]);
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}