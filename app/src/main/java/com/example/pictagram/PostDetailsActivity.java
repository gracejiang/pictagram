package com.example.pictagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pictagram.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailsActivity";

    Post post;

    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private ImageView ivPicture;
    private TextView tvDescription;
    private TextView tvCreatedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        ivProfilePicture = findViewById(R.id.post_profile_picture);
        tvUsername = findViewById(R.id.post_username_tv);
        ivPicture = findViewById(R.id.post_picture_iv);
        tvDescription = findViewById(R.id.post_description_tv);
        tvCreatedAt = findViewById(R.id.post_created_at);

        setPostValues();
    }

    private void setPostValues() {

        // load profile pic
        ParseFile profilePic = (ParseFile) post.getUser().get("profilePic");
        if (profilePic != null) {
            Glide.with(this)
                    .load(httpToHttps(profilePic.getUrl()))
                    .circleCrop()
                    .into(ivProfilePicture);
        } else {
            Log.e(TAG, "couldn't load profile pic");
        }

        // load username
        String username = post.getUser().getUsername();
        tvUsername.setText(username);

        // load post image
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this)
                    .load(httpToHttps(image.getUrl()))
                    .into(ivPicture);
        }

        // load description
        String descriptionString = "<b>" + username + "</b> " + post.getDescription();
        tvDescription.setText(Html.fromHtml(descriptionString));

        // load created at
        tvCreatedAt.setText(dateToString(post.getCreatedAt()));
    }

    // converts http to https
    private String httpToHttps(String url) {
        if (url.contains("https")) {
            return url;
        }

        String httpsUrl = "https" + url.substring(4);
        return httpsUrl;
    }

    // raw date to relative string
    private String dateToString(Date rawDate) {
        String relativeDate = "";

        long dateMillis = rawDate.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return relativeDate;
    }
}