package com.example.energygymapp.presentation.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.energygymapp.R;
import com.example.energygymapp.data.constants.DbConstants;
import com.example.energygymapp.data.repository.AuthRepository;
import com.example.energygymapp.data.repository.UserRepository;
import com.example.energygymapp.domain.util.OnResultListener;
import com.example.energygymapp.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1001;

    private ActivityRegisterBinding binding;
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        Glide.with(this).load(uri).circleCrop().into(binding.ivProfilePicture);
                    }
                });

        binding.ivProfilePicture.setOnClickListener(v -> requestImagePermissionAndPick());
        binding.btnSelectPhoto.setOnClickListener(v -> requestImagePermissionAndPick());

        binding.btnRegister.setOnClickListener(v -> attemptRegister());

        binding.tvLoginLink.setOnClickListener(v -> finish());
    }

    private void requestImagePermissionAndPick() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            imagePickerLauncher.launch("image/*");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePickerLauncher.launch("image/*");
            } else {
                Toast.makeText(this, "Для выбора фото необходимо разрешение на доступ к галерее", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void attemptRegister() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        AuthRepository.firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        setLoading(false);
                        Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String uid = user.getUid();
                    if (selectedImageUri != null) {
                        uploadProfileImage(uid, name, email, phone, selectedImageUri);
                    } else {
                        saveUserAndNavigate(uid, name, email, phone, "");
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void uploadProfileImage(String uid, String name, String email, String phone, Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child(DbConstants.STORAGE_PROFILE_IMAGES)
                .child(uid);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveUserAndNavigate(uid, name, email, phone, uri.toString()))
                        .addOnFailureListener(e -> saveUserAndNavigate(uid, name, email, phone, "")))
                .addOnFailureListener(e -> saveUserAndNavigate(uid, name, email, phone, ""));
    }

    private void saveUserAndNavigate(String uid, String name, String email, String phone, String imageUrl) {
        UserRepository userRepository = new UserRepository();
        userRepository.saveUserData(uid, name, email, phone, imageUrl, new OnResultListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                goToMain();
            }

            @Override
            public void onFailure() {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(RegisterActivity.this, "Ошибка сохранения данных", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        binding.btnRegister.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
