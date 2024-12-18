package com.example.pothole.Authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pothole.DashboardScreen.MainActivity;
import com.example.pothole.R;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pothole-060104-default-rtdb.firebaseio.com/");

    Button btnSignIn, btnGoogle;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Khai báo Firebase Auth và Google Sign-In
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_id))  // Lấy từ Firebase Console
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khai báo các thành phần
        final EditText etPassword = findViewById(R.id.etPassword);
        final EditText etEmail = findViewById(R.id.etEmail);
        final TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        final TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnGoogle = findViewById(R.id.btnGoogle);

        // Đăng nhập bằng email và mật khẩu
        btnSignIn.setOnClickListener(view -> {
            final String email = etEmail.getText().toString();
            final String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng nhập Firebase với email và mật khẩu
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(login.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        btnGoogle.setOnClickListener(view -> signInWithGoogle());


        tvCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(login.this, register.class);
            startActivity(intent);
        });


        tvForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(login.this, Forgetpassword.class);
            startActivity(intent);
        });

    }

    private void signInWithGoogle() {
        // Đăng xuất trước khi đăng nhập lại
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    // Đăng nhập Firebase bằng Google Token
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    Toast.makeText(this, "Google sign-in failed: account is null", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }



    }

    // Đăng nhập Firebase bằng Google Token
    private void firebaseAuthWithGoogle(String idToken) {
        if (idToken == null) {
            Toast.makeText(this, "Google ID Token is null", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(login.this, "Login successful with Google", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(login.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Đăng xuất người dùng
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                .addOnCompleteListener(this, task -> {
                    // Sau khi đăng xuất, chuyển về màn hình đăng nhập
                    Intent intent = new Intent(this, login.class);
                    startActivity(intent);
                    finish();
                });
    }
}
