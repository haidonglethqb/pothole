package com.example.pothole;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class OTP extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth = FirebaseAuth.getInstance();
        Button btnConfirmOtp = findViewById(R.id.btnConfirmOtp);

        btnConfirmOtp.setOnClickListener(view -> {
            // Kiểm tra nếu người dùng đã đăng nhập
            if (mAuth.getCurrentUser() != null) {
                FirebaseAuth.getInstance().getCurrentUser().reload();  // Reload lại người dùng để kiểm tra trạng thái xác thực

                // Kiểm tra email đã được xác thực
                if (mAuth.getCurrentUser().isEmailVerified()) {
                    Toast.makeText(OTP.this, "Email verified successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OTP.this, login.class);  // Chuyển đến màn hình đăng nhập
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OTP.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Người dùng chưa đăng nhập
                Toast.makeText(OTP.this, "No user is currently logged in.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
