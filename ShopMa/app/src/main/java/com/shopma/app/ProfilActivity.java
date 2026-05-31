package com.shopma.app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfilActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "shopma_prefs";
    private static final String KEY_PROFIL_NOM = "profil_nom";
    private static final String KEY_PROFIL_EMAIL = "profil_email";
    private static final String KEY_PROFIL_ADRESSE = "profil_adresse";
    private static final String KEY_PROFIL_PHOTO = "profil_photo_path";
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private EditText etNom, etEmail, etAdresse;
    private ImageView ivProfilePhoto;
    private SharedPreferences prefs;
    private String currentPhotoPath;
    private String username;


    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && currentPhotoPath != null) {
                    loadPhotoFromPath(currentPhotoPath);
                    // Save photo path
                    prefs.edit().putString(KEY_PROFIL_PHOTO, currentPhotoPath).apply();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        username = getIntent().getStringExtra("username");
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        setupViews();
        loadProfile();
    }

    private void setupViews() {
        etNom = findViewById(R.id.etNom);
        etEmail = findViewById(R.id.etEmailProfil);
        etAdresse = findViewById(R.id.etAdresse);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        Button btnChangePhoto = findViewById(R.id.btnChangePhoto);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnHistorique = findViewById(R.id.btnHistorique);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnChangePhoto.setOnClickListener(v -> checkCameraPermissionAndLaunch());
        btnSave.setOnClickListener(v -> saveProfile());
        btnHistorique.setOnClickListener(v -> openHistorique());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadProfile() {
        // Populate fields from SharedPreferences
        String savedEmail = prefs.getString(KEY_PROFIL_EMAIL,
                prefs.getString("saved_email", username != null ? username + "@shopma.ma" : ""));
        etNom.setText(prefs.getString(KEY_PROFIL_NOM, username != null ? username : ""));
        etEmail.setText(savedEmail);
        etAdresse.setText(prefs.getString(KEY_PROFIL_ADRESSE, ""));

        // Load saved photo
        String photoPath = prefs.getString(KEY_PROFIL_PHOTO, null);
        if (photoPath != null) {
            loadPhotoFromPath(photoPath);
        }
    }

    private void saveProfile() {
        String nom = etNom.getText().toString().trim();
        String adresse = etAdresse.getText().toString().trim();

        prefs.edit()
                .putString(KEY_PROFIL_NOM, nom)
                .putString(KEY_PROFIL_EMAIL, etEmail.getText().toString().trim())
                .putString(KEY_PROFIL_ADRESSE, adresse)
                .apply();

        Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_SHORT).show();
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        }
    }

    private void launchCamera() {
        try {
            File photoFile = createImageFile();
            currentPhotoPath = photoFile.getAbsolutePath();
            Uri photoUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(photoUri);
        } catch (IOException e) {
            Toast.makeText(this, "Erreur lors de la création du fichier photo", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "SHOPMA_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void loadPhotoFromPath(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            if (bitmap != null) {
                ivProfilePhoto.setImageBitmap(bitmap);
            }
        }
    }

    private void openHistorique() {
        Intent intent = new Intent(this, CommandesActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void logout() {

        prefs.edit()
                .putBoolean("logged_in", false)
                .apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, R.string.camera_permission_denied,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
