package com.example.lifemeup.addapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lifemeup.R;
import com.example.lifemeup.model.CommentModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//Create a custom RecyclerView adapter for displaying comments
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    Context cntxt;
    List<CommentModel> list; //a list for the comments

    //Constructor for the CommentAdapter class
    public CommentAdapter(Context cntxt, List<CommentModel> list) {
        this.cntxt= cntxt;
        this.list= list;
    }
    //Create the ViewHolder class that contains the layout of a comment element
    static class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView name, comment;
        CircleImageView profileImageUrl;

        //Constructor for the CommentViewHolder
        //initializing the view elements
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            comment= itemView.findViewById(R.id.comment);
            profileImageUrl= itemView.findViewById(R.id.profile_image);
            name= itemView.findViewById(R.id.name);


        }
    }
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        //Load the profile image
        Glide.with(cntxt).load(list.get(position).getProfileImageUrl())
                .placeholder(R.drawable.user_60).into(holder.profileImageUrl);
        //Load the user's comment
        holder.comment.setText(list.get(position).getComment());
        //Load the username of the user
        holder.name.setText(list.get(position).getName());


    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view, parent, false);
        return new CommentViewHolder(view);
    }


    //Returns the amount of comments
    @Override
    public int getItemCount() {
        return list.size();
    }


}
