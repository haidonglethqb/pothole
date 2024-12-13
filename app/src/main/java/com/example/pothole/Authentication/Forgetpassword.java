package com.example.pothole.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pothole.R;
import com.google.firebase.auth.FirebaseAuth;

public class Forgetpassword extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        mAuth = FirebaseAuth.getInstance();

        final EditText etEmail = findViewById(R.id.etEmail);
        final Button btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(Forgetpassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi email reset password qua Firebase Auth
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Forgetpassword.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();

                            // Sau khi gửi email, chuyển sang màn hình OTP để người dùng xác thực
                            Intent intent = new Intent(Forgetpassword.this, OTP.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Forgetpassword.this, "Failed to send reset link", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
