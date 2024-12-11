package com.example.pothole;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class EditProfile extends BaseActivity {
    private ImageView ivBack;
    private EditText etName, etEmail, etPhoneNumber,edDateOfBirth;
    private TextView  tvCountryRegion;
    private Button btnSaveChanges;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Tham chiếu các thành phần giao diện
        ivBack = findViewById(R.id.ivBack);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        edDateOfBirth = findViewById(R.id.edDateOfBirth);
        tvCountryRegion = findViewById(R.id.tvCountryRegion);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Gán sự kiện cho nút Back
        ivBack.setOnClickListener(v -> finish());

        // Tải dữ liệu từ SharedPreferences
        loadProfileData();

        // Gán sự kiện cho nút Save
        btnSaveChanges.setOnClickListener(v -> saveProfileData());
    }

    private void loadProfileData() {
        // Lấy dữ liệu từ SharedPreferences
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        String dateOfBirth = sharedPreferences.getString("dateOfBirth", "");
        String countryRegion = sharedPreferences.getString("countryRegion", "");

        // Hiển thị dữ liệu lên giao diện
        etName.setText(name);
        etEmail.setText(email);
        etPhoneNumber.setText(phoneNumber);
        edDateOfBirth.setText(dateOfBirth);
        tvCountryRegion.setText(countryRegion);
    }

    private void saveProfileData() {
        // Lấy thông tin từ giao diện
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String dateOfBirth = edDateOfBirth.getText().toString().trim();
        String countryRegion = tvCountryRegion.getText().toString().trim();

        // Lưu dữ liệu vào SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("dateOfBirth", dateOfBirth);
        editor.putString("countryRegion", countryRegion);
        editor.apply();

        Toast.makeText(this, "Saved Changes", Toast.LENGTH_SHORT).show();
    }
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

}
