package com.example.pictagram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pictagram.EditProfileActivity;
import com.example.pictagram.LoginActivity;
import com.example.pictagram.MainActivity;
import com.example.pictagram.R;
import com.example.pictagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    ParseUser currentUser;

    private ImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvBio;
    private Button btnEditProfile;
    private Button btnLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUser = ParseUser.getCurrentUser();

        // view bindings
        ivAvatar = view.findViewById(R.id.profile_pfp);
        tvUsername = view.findViewById(R.id.profile_username);
        tvBio = view.findViewById(R.id.profile_bio);
        btnEditProfile = view.findViewById(R.id.profile_edit_button);
        btnLogout = view.findViewById(R.id.profile_logout);

        // update fields

        // profile pic
        ParseFile profilePic = (ParseFile) currentUser.get("profilePic");
        if (profilePic != null) {
            Glide.with(getContext())
                    .load(httpToHttps(profilePic.getUrl()))
                    .circleCrop()
                    .into(ivAvatar);
        } else {
            Log.e(TAG, "couldn't load profile pic");
        }

        // username
        tvUsername.setText(currentUser.getUsername());
        tvBio.setText(currentUser.get("bio").toString());


//        rvPosts = view.findViewById(R.id.posts_rv);
//
//        // create adapter
//        postsAdapter = new PostsAdapter(getContext(), allPosts);
//
//        // set adapter on recycler view
//        rvPosts.setAdapter(postsAdapter);
//
//        // set layout manager on recycler view
//        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPosts();

        // edit profile functionality
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to edit activity
                Intent i = new Intent(getContext(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        // logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    // log out functionality
    private void logOut() {
        ParseUser.logOut();
        // ParseUser currentUser = ParseUser.getCurrentUser();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    // retrieve posts from parse database
    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> posts, ParseException e) {
                // on successfully retrieving posts, do operations here
                if (e == null) {
                    for (Post post : posts) {
                        // uncomment line below to view posts
                        Log.i(TAG, "Post " + post.getDescription() + ", by " + post.getUser().getUsername());
                    }

                } else {
                    Log.d(TAG, "Issue with retrieving posts: " + e.getMessage());
                }

//                allPosts.addAll(posts);
//                postsAdapter.notifyDataSetChanged();
            }
        });
    }

    // converts http link to https
    private String httpToHttps(String url) {
        if (url.contains("https")) {
            return url;
        }

        String httpsUrl = "https" + url.substring(4);
        return httpsUrl;
    }
}