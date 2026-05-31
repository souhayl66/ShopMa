package com.shopma.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "shopma_prefs";
    private static final String KEY_SAVED_EMAIL = "saved_email";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_USERNAME = "username";

    private EditText etEmail, etPassword;
    private CheckBox cbRememberMe;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        if (prefs.getBoolean(KEY_LOGGED_IN, false)) {
            goToDashboard(prefs.getString(KEY_USERNAME, ""));
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        Button btnLogin = findViewById(R.id.btnLogin);


        if (prefs.getBoolean(KEY_REMEMBER_ME, false)) {
            String savedEmail = prefs.getString(KEY_SAVED_EMAIL, "");
            etEmail.setText(savedEmail);
            cbRememberMe.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }


        if (password.length() < 6) {
            Toast.makeText(this, R.string.error_password_length, Toast.LENGTH_SHORT).show();
            return;
        }


        String validEmail = getString(R.string.valid_email);
        String validPassword = getString(R.string.valid_password);

        if (!email.equals(validEmail) || !password.equals(validPassword)) {
            Toast.makeText(this, R.string.error_invalid_credentials, Toast.LENGTH_SHORT).show();
            return;
        }


        SharedPreferences.Editor editor = prefs.edit();
        if (cbRememberMe.isChecked()) {
            editor.putBoolean(KEY_REMEMBER_ME, true);
            editor.putString(KEY_SAVED_EMAIL, email);
        } else {
            editor.putBoolean(KEY_REMEMBER_ME, false);
            editor.remove(KEY_SAVED_EMAIL);
        }
        editor.putBoolean(KEY_LOGGED_IN, true);


        String username = email.substring(0, email.indexOf('@'));
        editor.putString(KEY_USERNAME, username);
        editor.apply();

        goToDashboard(username);
    }

    private void goToDashboard(String username) {
        Intent intent = new Intent(this, AccueilActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}
