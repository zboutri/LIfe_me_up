package com.example.lifemeup.addapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lifemeup.R;
import com.example.lifemeup.model.PostModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Date;
import java.util.List;

public class PostImageAdapter extends FirestoreRecyclerAdapter<PostModel, PostImageAdapter.PostImageHolder> {

    List<PostModel> postModelList;
    OnImageClicked onImageClicked;

    public PostImageAdapter(@NonNull FirestoreRecyclerOptions<PostModel> options, List<PostModel> postModelList) {
        super(options);
        this.postModelList = postModelList;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostImageHolder holder, int position, @NonNull PostModel model) {
        // Load and display the image
        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(model.getImageUrl())
                .timeout(6500)
                .into(holder.imageView);
        // Set a click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageClicked.onClicked(
                        postModelList.get(holder.getAbsoluteAdapterPosition()).getImageUrl(),
                        postModelList.get(holder.getAbsoluteAdapterPosition()).getDescription(),
                        postModelList.get(holder.getAbsoluteAdapterPosition()).getUid(),
                        postModelList.get(holder.getAbsoluteAdapterPosition()).getTime());
            }
        });
    }

    @NonNull
    @Override
    public PostImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_view,parent, false);

        return new PostImageHolder(view);

    }

    @Override
    public int getItemCount(){
        return postModelList.size();
    }

    static class PostImageHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        public PostImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public void OnImageClicked( OnImageClicked onImageClicked){
        this.onImageClicked = onImageClicked;
    }
    // Define the interface for image click events
    public interface OnImageClicked{
        void onClicked(String imageUrl, String description, String uid, Date timestamp );
    }
}
