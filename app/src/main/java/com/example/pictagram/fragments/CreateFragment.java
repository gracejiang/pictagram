package com.example.pictagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pictagram.R;
import com.example.pictagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/* TODO
 ** shrink resolution & add rotations https://guides.codepath.org/android/Accessing-the-Camera-and-Stored-Media
 */

public class CreateFragment extends Fragment {

    public static final String TAG = "CreateFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    public static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 2;

    EditText etDescription;
    Button btnTakePicture;
    Button btnUploadPicture;
    ImageView ivPostImage;
    Button btnSubmit;

    private File photoFile;
    private String photoFileName = "pic.jpg";

    Bitmap bitmap;
    boolean uploadedPic = false;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        etDescription = view.findViewById(R.id.create_description);
        btnTakePicture = view.findViewById(R.id.create_take_pic_btn);
        btnUploadPicture = view.findViewById(R.id.create_upload_pic_btn);
        ivPostImage = view.findViewById(R.id.create_image_preview);
        btnSubmit = view.findViewById(R.id.create_post_btn);

        // launch camera activity
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadedPic = false;
                launchCamera();
            }
        });

        // launch gallery activity
        btnUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadedPic = true;
                launchGallery();
            }
        });


        // submit post
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etDescription.getText().toString();
                if (description.isEmpty()) {
                    description = " ";
                }

                if (ivPostImage.getDrawable() == null) {
                    makeToast("Please select an image to post!");
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();
                if (uploadedPic) {
                    savePost(description, currentUser, bitmap);
                } else {
                    savePost(description, currentUser, photoFile);
                }
            }
        });

    }

    // launch camera application to take picture
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // launch gallery application to upload picture
    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // camera roll
        if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    bitmap = loadFromUri(uri);
                    ivPostImage.setImageBitmap(bitmap);
                } catch (NullPointerException e) {
                    Log.e(TAG, "null pointer exception when uploading pic " + e);
                    makeToast("Null pointer exception when uploading pic.");
                }
            } else { // result failed
                makeToast("Picture wasn't uploaded.");
            }
            Log.i(TAG, "uploaded picture");
        }

        // take camera
        else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivPostImage.setImageBitmap(takenImage);
            } else { // result failed
                makeToast("Picture wasn't taken.");
            }
            Log.i(TAG, "taken picture");
        }
    }

    // load image from Uri
    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    // returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // get safe storage directory for photos
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }


    // for uploading pics
    private void savePost(String description, ParseUser currentUser, Bitmap bitmap) {
        Post post = new Post();
        post.setDescription(description);

        // compresses image to lower quality
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        // bitmap --> rbg byte[]
        byte[] image = stream.toByteArray();

        // reads in byte[] as file
        post.setImage(new ParseFile(image));
        post.setUser(currentUser);

        // save post object to parse database
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while posting your picture", e);
                    makeToast("Error while posting your picture.");
                    return;
                }

                makeToast("Your picture was posted!");
                etDescription.setText("");
                ivPostImage.setImageResource(0);
            }
        });
    }


    // saves post to database
    private void savePost(String description, ParseUser currentUser, File photoFile) {
        // create post object & set its attributes
        Post post = new Post();
        post.setDescription(description);
//        Glide.with(getContext())
//                .load(new File(photoFile.toURI())) // Uri of the picture
//                //.transform(new CircleTransform(..))
//                .into(ivPostImage);

        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);

        // save post object to parse database
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while posting your picture", e);
                    makeToast("Error while posting your picture.");
                    return;
                }

                makeToast("Your picture was posted!");
                etDescription.setText("");
                ivPostImage.setImageResource(0);
            }
        });
    }


    private void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


}
