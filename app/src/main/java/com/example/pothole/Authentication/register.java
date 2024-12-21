package com.example.pothole.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pothole.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class register extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các View và FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        final EditText etEmail = findViewById(R.id.etEmail);
        final EditText etPassword = findViewById(R.id.etPassword);
        final EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        final Button btnSignUp = findViewById(R.id.btnSignUp);
        final TextView tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount);
        final ImageButton btnGoogle = findViewById(R.id.btnGoogle);
        final ImageButton btnFacebook = findViewById(R.id.btnFacebook);


        btnSignUp.setOnClickListener(view -> {
            final String email = etEmail.getText().toString();
            final String password = etPassword.getText().toString();
            final String confirmPassword = etConfirmPassword.getText().toString();

            // Kiểm tra nhập
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(register.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }


            if (password.length() < 8) {
                Toast.makeText(register.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra mật khẩu và xác nhận mật khẩu
            if (!password.equals(confirmPassword)) {
                Toast.makeText(register.this, "Password does not match", Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Gửi email xác thực
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Thông báo email xác thực đã được gửi
                                            Toast.makeText(register.this, "Verification email sent.", Toast.LENGTH_SHORT).show();

                                            // Chuyển hướng người dùng sang màn hình OTP để kiểm tra email
                                            Intent intent = new Intent(register.this, OTP.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(register.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        tvAlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(register.this, login.class);
            startActivity(intent);
        });
    }

}
