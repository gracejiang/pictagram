package com.example.pictagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.pictagram.functions.SimpleDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    TextInputLayout etUsername;
    TextInputLayout etPassword;
    Button btnLogin;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        etUsername = findViewById(R.id.login_username);
        etPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.login_button);
        btnRegister = findViewById(R.id.login_register_instead);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getEditText().getText().toString();
                String password = etPassword.getEditText().getText().toString();

                loginUser(username, password);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRegisterActivity();
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    // report issue with login

                    wrongPasswordNotification();
                    Log.e(TAG, "issue with login: ", e);
                    return;
                }

                // successful login
                goMainActivity();
            }
        });

    }

    private void wrongPasswordNotification() {
        SimpleDialog simpleDialog = new SimpleDialog("Login Error",
                "Invalid username or password. Please try again.");
        simpleDialog.show(getSupportFragmentManager(), "login error dialog");
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goRegisterActivity() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        finish();
    }

}