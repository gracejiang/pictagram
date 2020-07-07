package com.example.pictagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pictagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    public static final String TAG = "UploadActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;

    EditText etDescription;
    Button btnTakePicture;
    ImageView ivPostImage;
    Button btnSubmit;
    private File photoFile;
    private String photoFileName = "pic.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        etDescription = findViewById(R.id.upload_description);
        btnTakePicture = findViewById(R.id.upload_take_pic_btn);
        ivPostImage = findViewById(R.id.upload_image_preview);
        btnSubmit = findViewById(R.id.upload_submit_btn);

        // launch camera activity
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });


        // submit post
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etDescription.getText().toString();
                if (description.isEmpty()) {
                    makeToast("Post description cannot be empty.");
                    return;
                } else if (photoFile == null || ivPostImage.getDrawable() == null) {
                    makeToast("There is no image!");
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);
            }
        });

    }

    // launch camera application to take picture
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(UploadActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivPostImage.setImageBitmap(takenImage);
            } else { // result failed
                makeToast("Picture wasn't taken.");
            }
        }
    }

    // returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // get safe storage directory for photos
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }


    // saves post to database
    private void savePost(String description, ParseUser currentUser, File photoFile) {
        // create post object & set its attributes
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
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

                makeToast("Your picture was uploaded!");
                etDescription.setText("");
                ivPostImage.setImageResource(0);
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