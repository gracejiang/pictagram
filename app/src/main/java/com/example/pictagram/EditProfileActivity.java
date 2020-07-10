package com.example.pictagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseUser;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = "EditProfileActivity";

    ParseUser currentUser;

    ImageView ivAvatar;
    Button btnEditAvatar;
    TextInputLayout etBio;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        currentUser = ParseUser.getCurrentUser();

        // view bindings
        ivAvatar = findViewById(R.id.edit_profile_avatar);
        btnEditAvatar = findViewById(R.id.edit_profile_edit_avatar_btn);
        etBio = findViewById(R.id.edit_profile_bio);
        btnSave = findViewById(R.id.edit_profile_save_btn);

        // update bio to say bio
        String currBio = currentUser.get("bio").toString();
        if (currBio.equals(" ")) {
            etBio.getEditText().setText("");
        } else {
            etBio.getEditText().setText(currBio);
        }


        // save button
        // ask how to navigate back to same tab
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewProfile();
            }
        });
    }

    // return to current activity
    private void saveNewProfile() {
        currentUser.put("bio", getBioText());
        currentUser.saveInBackground();
    }

    private String getBioText() {
        String currBio = etBio.getEditText().getText().toString();
        if (currBio.equals("")) {
            return " ";
        }
        return currBio;
    }


}