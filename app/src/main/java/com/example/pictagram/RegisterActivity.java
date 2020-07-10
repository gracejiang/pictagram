package com.example.pictagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.pictagram.functions.SimpleDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";

    TextInputLayout etUsername;
    TextInputLayout etPassword;
    TextInputLayout etPasswordConfirmation;
    Button btnRegister;
    Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.register_username);
        etPassword = findViewById(R.id.register_password);
        etPasswordConfirmation = findViewById(R.id.register_password_confirmation);

        btnRegister = findViewById(R.id.register_button);
        btnLogin = findViewById(R.id.register_login_instead);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getEditText().getText().toString();
                String password = etPassword.getEditText().getText().toString();
                String passwordConfirmation = etPasswordConfirmation.getEditText().getText().toString();

                if (username.length() == 0) {
                    makeNotification("Register Error","Your username cannot be empty. Please try again.");
                } else if (password.length() == 0) {
                    makeNotification("Register Error","Your password cannot be empty. Please try again.");
                } else if (!password.equals(passwordConfirmation)) {
                    makeNotification("Register Error","Please make sure your passwords match and try again.");
                } else {
                    registerAccount(username, password);
                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLoginActivity();
            }
        });
    }

    private void makeNotification(String title, String message) {
        SimpleDialog simpleDialog = new SimpleDialog(title, message);
        simpleDialog.show(getSupportFragmentManager(), "register error dialog");
    }

    private void registerAccount(String username, String password) {
        // Create the ParseUser
        ParseUser user = new ParseUser();

        // Set core properties
        user.setUsername(username);
        user.setPassword(password);

        // set custom properties
        user.put("bio", " ");

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    goMainActivity();
                } else {
                    makeNotification("Register Error",
                            "There was an error with your registration. Please try again later.");
                    Log.e(TAG, "Parse Exception", e);
                }
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

}
