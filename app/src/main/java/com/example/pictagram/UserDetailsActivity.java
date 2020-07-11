package com.example.pictagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class UserDetailsActivity extends AppCompatActivity {

    public static final String TAG = "UserDetailsActivity";

    ParseUser user;

    private ImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // view bindings
        ivAvatar = findViewById(R.id.user_details_pfp);
        tvUsername = findViewById(R.id.user_details_username);
        tvBio = findViewById(R.id.user_details_bio);

        setUserValues();
    }

    private void setUserValues() {
        // load profile pic
        ParseFile profilePic = (ParseFile) user.get("profilePic");
        if (profilePic != null) {
            Glide.with(this)
                    .load(httpToHttps(profilePic.getUrl()))
                    .circleCrop()
                    .into(ivAvatar);
        } else {
            Log.e(TAG, "couldn't load profile pic");
        }

        // load username
        String username = user.getUsername();
        tvUsername.setText(username);

        // load bio
        String bio = user.get("bio").toString();
        tvBio.setText(bio);
    }

    // converts http to https
    private String httpToHttps(String url) {
        if (url.contains("https")) {
            return url;
        }

        String httpsUrl = "https" + url.substring(4);
        return httpsUrl;
    }
}