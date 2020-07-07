package com.example.pictagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pictagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class UploadActivity extends AppCompatActivity {

    public static final String TAG = "UploadActivity";

    EditText etDescription;
    Button btnTakePicture;
    ImageView ivPostImage;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        etDescription = findViewById(R.id.upload_description);
        btnTakePicture = findViewById(R.id.upload_take_pic_btn);
        ivPostImage = findViewById(R.id.upload_image_preview);
        btnSubmit = findViewById(R.id.upload_submit_btn);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etDescription.getText().toString();
                if (description.isEmpty()) {
                    makeToast("Post description cannot be empty");
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser);
            }
        });

    }

    private void savePost(String description, ParseUser currentUser) {
        // create post object & set its attributes
        Post post = new Post();
        post.setDescription(description);
        // post.setImage();
        post.setUser(currentUser);

        // save post object to parse database
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    makeToast("Error while saving your post.");
                    return;
                }

                Log.i(TAG, "Post save was successful!");
                etDescription.setText("");
            }
        });
    }


    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // EVENTUALLY MOVE THIS INTO TIMELINE ACTIVITY
    // retrieve posts from parse database
    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);

        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> posts, ParseException e) {
                // on successfully retrieving posts, do operations here
                if (e == null) {
                    for (Post post : posts) {
                        // uncomment line below to view posts
                        // Log.i(TAG, "Post " + post.getDescription() + ", by " + post.getUser().getUsername());
                    }

                } else {
                    Log.d(TAG, "Issue with retrieving posts: " + e.getMessage());
                }

            }
        });
    }


}