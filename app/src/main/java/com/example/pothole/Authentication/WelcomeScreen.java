package com.example.pothole.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pothole.R;

public class WelcomeScreen extends AppCompatActivity {

    Button btn_login, btn_register;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_login = findViewById(R.id.btnLogin);
        btn_register = findViewById(R.id.btnRegister);

        btn_login.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeScreen.this, login.class);
            startActivity(intent);
        });

        btn_register.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeScreen.this, register.class);
            startActivity(intent);
        });

        imageView = findViewById(R.id.ivWelcomeImage);
        Glide.with(this).load(R.drawable.community).into(imageView);

    }

}