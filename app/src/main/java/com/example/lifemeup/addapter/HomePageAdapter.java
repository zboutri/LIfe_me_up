package com.example.lifemeup.addapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lifemeup.R;
import com.example.lifemeup.ReplaceActivity;
import com.example.lifemeup.model.HomePageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//Create a RecyclerView adapter for the home page
public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.HomePageHolder> {
    private final List<HomePageModel> homeModelList;
    Activity activity;

    BtnPressed pressed;

    //Constructor for the HomePageAdapter class
    public HomePageAdapter(List<HomePageModel> homeModelList, Activity activity) {
        this.homeModelList = homeModelList;
        this.activity = activity;
    }

    //Create a ViewHolder that contains the layout of home page elements
    class HomePageHolder extends RecyclerView.ViewHolder{
        private final CircleImageView profImage;
        private final TextView name, description, commentText;
        private final TextView hour,congratsCounter;
        private final ImageView postedImage;
        private final CheckBox congratsBtn;
        ImageButton comment;

        //Constructor for the HomePageHolder, initializing the elements
        public HomePageHolder(@NonNull View itemView) {
            super(itemView);

            profImage = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.name);
            hour = itemView.findViewById(R.id.hour);
            congratsCounter = itemView.findViewById(R.id.likes_counter);
            postedImage = itemView.findViewById(R.id.posted_image);
            description = itemView.findViewById(R.id.desc);
            congratsBtn = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            commentText = itemView.findViewById(R.id.commentText);
            commentText.setVisibility(View.VISIBLE);


        }

        //Contains all the listeners for different elements
        public void listeners(final int position, final String id, final List<String> congratsList, final String userID) {
            //add listener to open the comment page
            commentText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ReplaceActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("userID", userID);
                    intent.putExtra("commentFlag", true);

                    activity.startActivity(intent);
                }
            });
            //add listener to open the comment page
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(activity, ReplaceActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("userID", userID);
                    intent.putExtra("commentFlag", true);

                    activity.startActivity(intent);

                }
            });
            //add listener to the like button in order to change its condition
            congratsBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean pressedFlag) {
                    pressed.congratsBtnPressed(position, id, userID, congratsList, pressedFlag);

                }
            });
        }
    }
    // Interface to handle button press events
    public interface BtnPressed{
        void congratsBtnPressed(int position, String id, String userID, List<String> congratsList, boolean pressedFlag);

    }
    // Set the button press listener
    public void BtnPressed(BtnPressed pressed){
        this.pressed = pressed;
    }

    @NonNull
    @Override
    public HomePageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_view, parent, false);
        return new HomePageHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HomePageHolder holder, int position) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Take the data for hour description and name
        holder.hour.setText(""+homeModelList.get(position).getTime());
        holder.description.setText(homeModelList.get(position).getDescription());
        holder.name.setText(homeModelList.get(position).getUsername());


        List<String> congratsList = homeModelList.get(position).getCongratsCounter();
        holder.congratsCounter.setText(congratsList.size() + " Congrats!");

        holder.congratsBtn.setChecked(congratsList.contains(currentUser.getUid()));
        // Load profile image
        Glide.with(activity.getApplicationContext())
                .load(homeModelList.get(position).getProfImage()).placeholder(R.drawable.user_60)
                .timeout(7000).into(holder.profImage);
        // Load post image
        Glide.with(activity.getApplicationContext()).load(homeModelList.get(position).getImageUrl())
                .placeholder(new ColorDrawable(Color.argb(0, 0, 0, 0)))
                .timeout(7500).into(holder.postedImage);
        // Add listeners to the elements
        holder.listeners(position, homeModelList.get(position).getId(),
                homeModelList.get(position).getCongratsCounter(),
                homeModelList.get(position).getUid());

    }
    // Return the amount of elements in the list
    @Override
    public int getItemCount() {
        return homeModelList.size();
    }


}
