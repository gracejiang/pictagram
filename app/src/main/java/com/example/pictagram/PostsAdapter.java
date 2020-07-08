package com.example.pictagram;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";

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

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfilePicture;
        private TextView tvUsername;
        private ImageView ivPicture;
        private TextView tvDescription;
        private TextView tvCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePicture = itemView.findViewById(R.id.post_profile_picture);
            tvUsername = itemView.findViewById(R.id.post_username_tv);
            ivPicture = itemView.findViewById(R.id.post_picture_iv);
            tvDescription = itemView.findViewById(R.id.post_description_tv);
            tvCreatedAt = itemView.findViewById(R.id.post_created_at);
        }

        // bind data from post into view elements
        public void bind(Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());

            // post image
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(httpToHttps(post.getImage().getUrl()))
                        .into(ivPicture);
            }

            // avatar
            Glide.with(context)
                    .load("https://www.pngkey.com/png/detail/56-565977_vectors-download-icon-pikachu-moustache.png")
                    .into(ivProfilePicture);

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
    }

    private String dateToString(Date rawDate) {
        String relativeDate = "";

        long dateMillis = rawDate.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return relativeDate;
    }
}
