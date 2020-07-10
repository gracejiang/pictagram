package com.example.pictagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pictagram.functions.PostsAdapter;
import com.example.pictagram.R;
import com.example.pictagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {

    public static final String TAG = "PostsFragment";

    private SwipeRefreshLayout swipeContainer;

    private RecyclerView rvPosts;
    private PostsAdapter postsAdapter;

    // data source
    private List<Post> allPosts = new ArrayList<>();

    public PostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.posts_rv);

        // swipe to refresh
        swipeContainer = view.findViewById(R.id.posts_swipe_container);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "refreshing the page!");
                queryPosts();
            }
        });

        // recycler view adapter
        // (1) create adapter
        postsAdapter = new PostsAdapter(getContext(), allPosts);
        // (2) set adapter on recycler view
        rvPosts.setAdapter(postsAdapter);
        // (3) set layout manager on recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPosts();
    }

    // retrieve posts from parse database
    public void queryPosts() {
        allPosts.clear();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> posts, ParseException e) {
                // on successfully retrieving posts, do operations here
                if (e == null) {
                    for (Post post : posts) {
                        // uncomment line below to view posts
                    }
                    swipeContainer.setRefreshing(false);
                } else {
                    Log.e(TAG, "Issue with retrieving posts: " + e.getMessage());
                }

                allPosts.addAll(posts);
                postsAdapter.notifyDataSetChanged();
            }
        });
    }

}