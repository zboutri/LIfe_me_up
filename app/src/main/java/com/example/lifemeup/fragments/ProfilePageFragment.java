package com.example.lifemeup.fragments;

import static android.app.Activity.RESULT_OK;

import static com.example.lifemeup.MainActivity.IS_SEARCHED_USER;
import static com.example.lifemeup.MainActivity.USER_ID;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lifemeup.R;
import com.example.lifemeup.addapter.PostImageAdapter;
import com.example.lifemeup.ReplaceActivity;
import com.example.lifemeup.model.PostModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfilePageFragment extends Fragment {

    private TextView username, goal, friends_num, posts_num;
    private CircleImageView profile_img;
    private Button add;
    private RecyclerView recyclerView;
    private LinearLayout countLayout;
    private ImageButton menu, editProf;
    PostImageAdapter adapter;
    private FirebaseUser user;
    boolean isMyProfile = true;
    boolean isFollowed;
    String userUid;
    int count;
    List<Object> friendsList, myFriendsList;
    private List<PostModel> list;
    DocumentReference userReference, myReference;
    ActivityResultLauncher activityResultLauncher;
    OnImagePass onImagePass;


    public interface OnImagePass{
       void onCheck(String imageUrl, String description, String uid, Date timestamp);
    }

    @Override
      public void onAttach(@NonNull Context context) {
          super.onAttach(context);

          onImagePass = (OnImagePass) context;
      }


    public ProfilePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        myReference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());

        // If is a profile from search then load its data
        if(IS_SEARCHED_USER){
            userUid = USER_ID;
            isMyProfile = false;
            loadData();
        }else{
            isMyProfile = true;
            userUid = user.getUid();
        }
        // If is current user's profile hide the buttons that shouldn't appear
        if(isMyProfile){
            editProf.setVisibility(View.VISIBLE);
            add.setVisibility(View.GONE);
            menu.setVisibility(View.VISIBLE);
            countLayout.setVisibility(View.VISIBLE);
        }
        else{
            menu.setVisibility(View.GONE);
            editProf.setVisibility(View.GONE);
            add.setVisibility(View.VISIBLE);
        }

        userReference = FirebaseFirestore.getInstance().collection("Users")
                .document(userUid);
        load_basic_data();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager((new ManagerWrapper(getContext(), 3)));
        loadPostImage();
        recyclerView.setAdapter(adapter);


        clickListener();

    }

    private  void loadData(){

        myReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null){
                    Log.e("Tag_b", error.getMessage());
                    return;
                }
                if(value == null || !value.exists()){
                    return;
                }
                // Get the current user's friends
                myFriendsList = (List<Object>) value.get("friends");
            }
        });
    }

    private void clickListener(){
        // When the add btn is clicked
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the current user already follows them
                if(isFollowed){
                    // Remove each other form each other's friends list
                    friendsList.remove(user.getUid());
                    myFriendsList.remove(userUid);

                    final Map<String, Object> map = new HashMap<>();
                    map.put("friends", friendsList);

                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("friends", myFriendsList);

                    // Update firebase
                    userReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                add.setText("Add");

                                myReference.update(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(), "Unfollowed", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Log.e("Tag_3", task.getException().getMessage());
                                        }
                                    }
                                });
                            }else{
                                Log.e("Tag", ""+task.getException().getMessage());
                            }
                        }
                    });


                }else{
                    // Send notification for their friendship
                    createNotification();

                    // Add each other to each other's friends list
                    friendsList.add(user.getUid());

                    myFriendsList.add(userUid);

                    final Map<String, Object> map = new HashMap<>();
                    map.put("friends", friendsList);

                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("friends", myFriendsList);
                    // Update firebase
                    userReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                add.setText("Unfollow");
                                myReference.update(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(), "Followed", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Log.e("Tag_3_1", task.getException().getMessage());
                                        }
                                    }
                                });
                            }else{
                                Log.e("Tag", ""+task.getException().getMessage());
                            }
                        }
                    });

                }
            }
        });

        // Activity to crop image
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if(result.getResultCode() == RESULT_OK && result.getData()!=null){
                        CropImage.ActivityResult crop_result = CropImage.getActivityResult(result.getData());

                        Uri uri = crop_result.getUri();
                        uploadImage(uri);
                    }

                });
        // When edit is clicked
        editProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure the crop options
                CropImage.ActivityBuilder cropBuilder = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1);

                // Start the crop activity using the ActivityResultLauncher
                activityResultLauncher.launch(cropBuilder.getIntent(getContext()));
            }
        });

        // When menu is clicked
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(getContext(), menu);

                popupMenu.getMenuInflater().inflate(R.menu.button_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked
                        switch (menuItem.getItemId()){
                            // When the user wants to change their goal
                            case R.id.change_goal:
                                Intent intent1 = new Intent(getContext(), ReplaceActivity.class);

                                intent1.putExtra("newGoal", true);
                                intent1.putExtra("isComment", false);

                                getContext().startActivity(intent1);
                                return true;
                                // When the user want to logOut
                            case R.id.log_out:

                                Intent intent = new Intent(getContext(), ReplaceActivity.class);
                                FirebaseAuth.getInstance().signOut();
                                intent.putExtra("isComment", false);

                                getContext().startActivity(intent);
                                return true;


                        }

                        return true;
                    }
                });
                // Show the popup menu
                popupMenu.show();
            }
        });

        // When an image is clicked open it to a new window
        adapter.OnImageClicked(new PostImageAdapter.OnImageClicked() {
            @Override
            public void onClicked(String imageUrl, String description, String uid, Date timestamp) {
                onImagePass.onCheck(imageUrl, description, uid, timestamp);
            }
        });

    }

    // Initialize elements
    private void init(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        assert getActivity() != null;
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        username = view.findViewById(R.id.name);
        friends_num = view.findViewById(R.id.friends_num);
        posts_num = view.findViewById(R.id.posts_num);
        menu = view.findViewById(R.id.prof_menu);
        profile_img = view.findViewById(R.id.profile_image);
        goal = view.findViewById(R.id.goal);
        add = view.findViewById(R.id.add_btn);
        recyclerView = view.findViewById(R.id.recycler_view);
        countLayout = view.findViewById(R.id.count_layout);
        editProf = view.findViewById(R.id.edit_prof);
        list = new ArrayList<>();

    }

    // Load user's info
    private void load_basic_data(){

        userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Tag_0", error.getMessage());
                    return;
                }

                assert value != null;
                if(value.exists()){
                    String name = value.getString("name");
                    String status = value.getString("status");
                    String profileURL = value.getString("image_prof");

                    //sets the user's data from Firebase to the local values
                    username.setText(name);
                    goal.setText(status);
                    friendsList = (List<Object>) value.get("friends");
                    friends_num.setText(""+friendsList.size());

                    //sets user's image to the app
                    try{
                        Glide.with(getContext().getApplicationContext())
                                .load(profileURL)
                                .placeholder(R.drawable.user_60)
                                .timeout(6500)
                                .into(profile_img);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Determine the btns context
                    if(friendsList.contains(user.getUid())){
                        add.setText("Unfollow");
                        isFollowed = true;

                    }else{
                        add.setText("ADD");
                        isFollowed = false;

                    }

                }
            }
        });
    }

    // Load the posted images
    private void loadPostImage() {

        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(userUid);

        Query query = reference.collection("Post Images").orderBy("time");

             query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                         @Override
                         public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                             if(error != null || value == null)
                                 return;
                             // Fir every posted image add it to the list
                             for(QueryDocumentSnapshot snapshot : value){
                                PostModel image = snapshot.toObject(PostModel.class);
                                list.add(image);
                            }
                             adapter.notifyDataSetChanged();
                             // The amount of images
                             count = adapter.getItemCount();
                             posts_num.setText(""+ count);
                         }
                     });

        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();

        adapter = new PostImageAdapter(options, list);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // upload a new image
    private void uploadImage(Uri uri){

        // Create a reference to the Firebase Storage location where the image will be uploaded
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        // Upload the image
        reference.putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        // If the image is uploaded get the download URL
                        if(task.isSuccessful()){
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageURL = uri.toString();
                                            // Create a UserProfileChangeRequest to update the user's profile photo
                                            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                                            request.setPhotoUri(uri);
                                            // Update the user's profile photo
                                            user.updateProfile(request.build());

                                            Map<String, Object> map = new HashMap<>();
                                            map.put("image_prof", imageURL);

                                            FirebaseFirestore.getInstance().collection("Users")
                                                    .document(user.getUid())
                                                    .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful()){
                                                                Toast.makeText(getContext(), "Updated Successful", Toast.LENGTH_SHORT);

                                                            }
                                                            else{
                                                                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }else{
                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    // Create notification for frienship
    void createNotification(){

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notification");

        String id = reference.document().getId();

        // Store the notification's data
        Map<String, Object> map = new HashMap<>();
        map.put("time", FieldValue.serverTimestamp());
        map.put("notification", user.getDisplayName() + " followed you");
        map.put("id", id);
        map.put("uid", userUid);
        map.put("image", null);

        reference.document(id).set(map);
    }
}

