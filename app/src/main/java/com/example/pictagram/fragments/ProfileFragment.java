package com.example.pictagram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pictagram.LoginActivity;
import com.example.pictagram.PostsAdapter;
import com.example.pictagram.R;
import com.example.pictagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

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

        btnLogout = view.findViewById(R.id.profile_logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });


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
}