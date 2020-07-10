package com.example.pictagram.functions;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictagram.PostDetailsActivity;
import com.example.pictagram.R;
import com.example.pictagram.fragments.PostsFragment;
import com.example.pictagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

import org.parceler.Parcels;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";

    ParseUser currentUser;

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        currentUser = ParseUser.getCurrentUser();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivProfilePicture;
        private TextView tvUsername;
        private ImageButton btnDelete;
        private ImageView ivPicture;
        private TextView tvDescription;
        private TextView tvCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            ivProfilePicture = itemView.findViewById(R.id.item_profile_picture);
            tvUsername = itemView.findViewById(R.id.item_username_tv);
            btnDelete = itemView.findViewById(R.id.item_delete_btn);
            ivPicture = itemView.findViewById(R.id.item_picture_iv);
            tvDescription = itemView.findViewById(R.id.item_description_tv);
            tvCreatedAt = itemView.findViewById(R.id.item_created_at);
        }

        // bind data from post into view elements
        public void bind(final Post post) {

            // profile pic
            ParseFile profilePic = (ParseFile) post.getUser().get("profilePic");
            if (profilePic != null) {
                Glide.with(context)
                        .load(httpToHttps(profilePic.getUrl()))
                        .circleCrop()
                        .into(ivProfilePicture);
            } else {
                Log.e(TAG, "couldn't load profile pic");
            }

            // username
            String username = post.getUser().getUsername();
            tvUsername.setText(username);

            // delete button
            if (username.equals(currentUser.getUsername())) {
                btnDelete.setVisibility(View.VISIBLE);
            } else {
                btnDelete.setVisibility(View.GONE);
            }

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteImage(post);
                }
            });

            // post image
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(httpToHttps(image.getUrl()))
                        .into(ivPicture);
            }

            // description
            String descriptionString = "<b>" + username + "</b> " + post.getDescription();
            tvDescription.setText(Html.fromHtml(descriptionString));

            // created at
            tvCreatedAt.setText(dateToString(post.getCreatedAt()));
        }

        private String httpToHttps(String url) {
            if (url.contains("https")) {
                return url;
            }

            String httpsUrl = "https" + url.substring(4);
            return httpsUrl;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                context.startActivity(intent);
            }
        }
    }

    private String dateToString(Date rawDate) {
        String relativeDate = "";

        long dateMillis = rawDate.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return relativeDate;
    }

    private void deleteImage(Post post) {
        post.deleteInBackground();
        Toast.makeText(context, "Your post was deleted.", Toast.LENGTH_LONG).show();
    }

}
