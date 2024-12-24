package com.example.pothole.SettingScreen;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pothole.Other.UserProfile;
import com.example.pothole.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfile extends BaseActivity {
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;

    private ImageView ivBack, ivProfilePicture, ivEditProfilePicture;
    private EditText etName, etEmail, etPhoneNumber, edDateOfBirth;
    private TextView tvCountryRegion;
    private Button btnSaveChanges;
    private Spinner spCountry;

    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Tham chiếu các thành phần giao diện
        ivBack = findViewById(R.id.ivBack);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivEditProfilePicture = findViewById(R.id.ivEditProfilePicture);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        edDateOfBirth = findViewById(R.id.edDateOfBirth);
        spCountry = findViewById(R.id.spCountry);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Gán sự kiện cho nút Back
        ivBack.setOnClickListener(v -> finish());

        // Tải dữ liệu từ SharedPreferences
        loadProfileData();

        // Gán sự kiện cho nút Save
        btnSaveChanges.setOnClickListener(v -> saveProfileData());

        // Gán sự kiện thay đổi ảnh đại diện
        ivEditProfilePicture.setOnClickListener(v -> openImageOptions());
    }

    private void loadProfileData() {
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        String dateOfBirth = sharedPreferences.getString("dateOfBirth", "");
        String countryRegion = sharedPreferences.getString("countryRegion", "");
        String profileImage = sharedPreferences.getString("profileImage", null);


        etName.setText(name);
        etEmail.setText(email);
        etPhoneNumber.setText(phoneNumber);
        edDateOfBirth.setText(dateOfBirth);


        if (countryRegion != null) {
            for (int i = 0; i < spCountry.getCount(); i++) {
                if (spCountry.getItemAtPosition(i).toString().equals(countryRegion)) {
                    spCountry.setSelection(i);
                    break;
                }
            }
        }

        // Hiển thị ảnh đại diện nếu có
        if (profileImage != null) {
            Bitmap bitmap = base64ToBitmap(profileImage);
            ivProfilePicture.setImageBitmap(bitmap);
        }
    }

    private void saveProfileData() {
        // Lấy userId từ SharedPreferences hoặc tạo giá trị mặc định
        String userId = sharedPreferences.getString("userId", "defaultUserId");

        // Lấy thông tin từ các trường nhập liệu
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String dateOfBirth = edDateOfBirth.getText().toString().trim();
        String countryRegion = spCountry.getSelectedItem().toString();

        // Lấy ảnh đại diện từ ImageView
        ivProfilePicture.setDrawingCacheEnabled(true);
        ivProfilePicture.buildDrawingCache();
        Bitmap bitmap = ivProfilePicture.getDrawingCache();

        if (bitmap != null) {
            String encodedImage = bitmapToBase64(bitmap);

            // Lưu dữ liệu vào SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("phoneNumber", phoneNumber);
            editor.putString("dateOfBirth", dateOfBirth);
            editor.putString("countryRegion", countryRegion);
            editor.putString("profileImage", encodedImage);
            editor.apply();

            // Lưu dữ liệu lên Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Tạo đối tượng chứa dữ liệu người dùng
            UserProfile userProfile = new UserProfile(name, email, phoneNumber, dateOfBirth, countryRegion);

            databaseReference.setValue(userProfile)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                        }
                    });

            // Trả về kết quả cho Activity gọi
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", name);
            resultIntent.putExtra("profileImage", encodedImage);
            setResult(RESULT_OK, resultIntent);

        } else {
            Toast.makeText(this, "Failed to save profile picture.", Toast.LENGTH_SHORT).show();
        }
    }


    private void openImageOptions() {
        String[] options = {"Choose from Gallery", "Take a Photo"};
        new android.app.AlertDialog.Builder(this)
                .setTitle("Change Profile Picture")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        chooseImageFromGallery();
                    } else if (which == 1) {
                        takePhotoWithCamera();
                    }
                })
                .show();
    }

    private void chooseImageFromGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_GALLERY);
        }
    }

    private void takePhotoWithCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CAMERA && data != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
            }

            if (bitmap != null) {
                ivProfilePicture.setImageBitmap(bitmap);
            }
        }
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String updatedName = data.getStringExtra("name");
            if (updatedName != null) {
                etName.setText(updatedName); // tvName là TextView hiển thị tên trong MainActivity
            }
        }
    }

    // Chuyển Bitmap thành chuỗi Base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Chuyển chuỗi Base64 thành Bitmap
    private Bitmap base64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
