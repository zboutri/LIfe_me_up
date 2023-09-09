package com.example.lifemeup.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lifemeup.R;
import com.example.lifemeup.addapter.CommentAdapter;
import com.example.lifemeup.addapter.HomePageAdapter;
import com.example.lifemeup.model.CommentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CommentFragment extends Fragment {
    // UI elements
    ImageButton sendButton;
    RecyclerView commentRecyclerView;
    EditText commentText;
    // Adapter and data list for comments
    CommentAdapter commentAdapter;
    List<CommentModel> commentModelList;
    // IDs and references
    String id, userID;
    CollectionReference collectionReference;
    DocumentReference imageReference;
    private String imageUrl;
    //current user
    FirebaseUser currentUser;

    public CommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize UI and references
        init(view);

        // Set up Firestore references
        collectionReference = FirebaseFirestore.getInstance().collection("Users")
                .document(userID).collection("Post Images")
                .document(id).collection("Comments");

        imageReference = FirebaseFirestore.getInstance().collection("Users")
                .document(userID).collection("Post Images").document(id);

        // Load existed data for comments
        loadExistedData();

        addListeners();
    }

    private void addListeners() {

        // Add a listener for clicking on send button to post a comment
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentTxt = commentText.getText().toString();

                // When the comment is empty
                if(commentTxt.isEmpty()){
                    Toast.makeText(getContext(),"Add a comment", Toast.LENGTH_SHORT).show();
                    return;
                }


                String commentID = collectionReference.document().getId();
                // Create a map about comment data
                Map<String, Object> map = new HashMap<>();
                map.put("name", currentUser.getDisplayName());
                map.put("uid", currentUser.getUid());
                map.put("profileImageUrl", Objects.requireNonNull(currentUser.getPhotoUrl()).toString());

                map.put("postID", id);
                map.put("time", FieldValue.serverTimestamp());

                map.put("comment", commentTxt);
                map.put("commentID", commentID);

                // Load data to Firebase
                collectionReference.document(commentID)
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    commentText.setText("");
                                }else{
                                    Toast.makeText(getContext(), "There was an error in commenting:" + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                // Get the url of the commented image and send notification to the user
                imageReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                imageUrl = doc.getString("imageUrl");
                                createNotification(userID, imageUrl, commentTxt);
                            }
                        }
                    }
                });
            }
        });
    }
    // Load existing comments and listen for changes
    private void loadExistedData() {
        collectionReference.orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }

                if (value == null){
                    Toast.makeText(getContext(), "There are no comments", Toast.LENGTH_SHORT).show();
                    return;
                }

                commentModelList.clear();

                for(DocumentSnapshot snapshot:value){
                    CommentModel model= snapshot.toObject((CommentModel.class));
                    commentModelList.add(model);
                }

                commentAdapter.notifyDataSetChanged();
            }
        });

    }

    // Initialize the elements
    private void init(View view){

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sendButton = view.findViewById(R.id.sendButton);
        commentText = view.findViewById(R.id.commentText);
        commentRecyclerView = view.findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentModelList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), commentModelList);
        commentRecyclerView.setAdapter(commentAdapter);


        if(getArguments() == null)
            return;;
        // Retrieve user and post IDs from arguments
        userID = getArguments().getString("userID");
        id = getArguments().getString("id");

    }

    // Create a notification about the comment
    void createNotification(String userUid, String image_id, String comment){
        // Access the notification collection
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notification");

        String id = reference.document().getId();
        // Create map and pass the data to firebase
        Map<String, Object> map = new HashMap<>();
        map.put("time", FieldValue.serverTimestamp());
        map.put("notification", currentUser.getDisplayName() + " commented: " + comment);
        map.put("id", id);
        map.put("image", image_id);
        map.put("uid", userUid);

        reference.document(id).set(map);
    }


}