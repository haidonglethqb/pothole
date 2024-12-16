package com.example.pothole.SettingScreen;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.pothole.DashboardScreen.MainActivity;
import com.example.pothole.R;

import java.util.Locale;

public class ChooseLanguge extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_languge);
        ImageView btBack = findViewById(R.id.ivBack);
        Button btVietnamese = findViewById(R.id.btVietnamese);
        Button btEnglish = findViewById(R.id.btEnglish);

        // Xử lý sự kiện chọn ngôn ngữ
        btVietnamese.setOnClickListener(v -> setLocale("vi"));
        btEnglish.setOnClickListener(v -> setLocale("en"));
        btBack.setOnClickListener(v -> finish());
    }

    // Phương thức thay đổi ngôn ngữ
//    private void setLocale(String languageCode) {
//        // Thiết lập ngôn ngữ
//        Locale locale = new Locale(languageCode);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.setLocale(locale);
//        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
//
//        // Cập nhật lại màn hình hiện tại
//        Intent refresh = new Intent(this, ChooseLanguge.class);
//        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(refresh);
//        finish();
//
//        Intent intent = new Intent(this, Settings.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//        recreate();
//    }
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Lưu trạng thái ngôn ngữ
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .edit()
                .putString("language", languageCode)
                .apply();

        // Làm mới toàn bộ ứng dụng bằng cách khởi động lại màn hình chính
        Intent refresh = new Intent(this, ChooseLanguge.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(refresh);
        finish();
        Intent intent = new Intent(this, Settings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        Intent intentDashboard = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentDashboard);
        finish();

    }

}
