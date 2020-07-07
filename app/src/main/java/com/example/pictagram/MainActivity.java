package com.example.pictagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pictagram.fragments.CreateFragment;
import com.example.pictagram.fragments.PostsFragment;
import com.example.pictagram.fragments.ProfileFragment;
import com.example.pictagram.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;


/* TODO
* customize bottom nav menu
* https://guides.codepath.org/android/Bottom-Navigation-Views
*/

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "UploadActivity";

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // bottom navigation view
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        fragment = new PostsFragment();
                        break;
                    case R.id.menu_create:
                        fragment = new CreateFragment();
                        break;
                    case R.id.menu_profile:
                        fragment = new ProfileFragment();
                        break;
                    default:
                        fragment = null;
                }
                fragmentManager.beginTransaction().replace(R.id.main_frame_layout, fragment).commit();
                return true;
            }
        });

        // default selection
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

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