package com.example.lifemeup.addapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lifemeup.R;
import com.example.lifemeup.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private List<UserModel> userModelList;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    OnProfileClicked onProfileClicked;

    public UserAdapter(List<UserModel> userModelList){
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get user's name and description
        holder.name.setText(userModelList.get(position).getName());
        holder.desc.setText(userModelList.get(position).getStatus());
        // Load and display the user's profile image
        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(userModelList.get(position).getImage_prof())
                .placeholder(R.drawable.user_60)
                .timeout(6500)
                .into(holder.profileImage);
        // If the user is the current user, hide the view
        if(userModelList.get(position).getUid().equals(currentUser.getUid())){
            holder.relativeLayout.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RelativeLayout.LayoutParams(0,0));

        }
        else {
            holder.relativeLayout.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RelativeLayout.LayoutParams(900,200));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProfileClicked.onClicked(userModelList.get(position).getUid());
            }
        });

    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    static class UserHolder extends RecyclerView.ViewHolder{

        private final CircleImageView profileImage;
        private final RelativeLayout relativeLayout;
        private final TextView name, desc;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            profileImage = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
        }


    }
    // Set the callback for profile click event
    public void OnProfileClicked( OnProfileClicked onProfileClicked){

        this.onProfileClicked = onProfileClicked;
    }
    // Define the interface for profile click events
    public interface OnProfileClicked{
        void onClicked(String uid);
    }
}
