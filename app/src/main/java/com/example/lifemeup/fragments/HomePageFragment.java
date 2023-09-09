package com.example.lifemeup.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.lifemeup.R;
import com.example.lifemeup.addapter.HomePageAdapter;
import com.example.lifemeup.model.HomePageModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomePageFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textView;
    private FirebaseUser currentUser;
    private String imageUrl;
    HomePageAdapter homePageAdapter;
    private List<HomePageModel> homePageModelList;
    private int image = 0;
    Date currentDate = new Date();

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize elements
        init(view);

        homePageModelList = new ArrayList<>();
        homePageAdapter = new HomePageAdapter(homePageModelList, getActivity());
        recyclerView.setAdapter(homePageAdapter);

        loadFirestoreData();

        // Add btn pressed listener from home page adapter
        homePageAdapter.BtnPressed(new HomePageAdapter.BtnPressed(){

            @Override
            public void congratsBtnPressed(int position, String id, String userID, List<String> likeList, boolean pressedFlag) {
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users")
                        .document(userID)
                        .collection("Post Images")
                        .document(id);

                image = 0;

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists())
                                imageUrl = doc.getString("imageUrl");

                            if(likeList.contains(currentUser.getUid())){
                                likeList.remove(currentUser.getUid()); //unlike

                            }else{
                                likeList.add(currentUser.getUid()); //like
                                createNotification(userID, imageUrl); // Create notification to notify user
                            }
                            // Flag in order to do it only 1 time
                            if(image == 0){
                                // Update congrats list
                                Map<String, Object> map = new HashMap<>();
                                map.put("congratsCounter", likeList);
                                image = 1;
                                documentReference.update(map);
                                homePageAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        });

    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadFirestoreData() {

        final DocumentReference docReference = FirebaseFirestore.getInstance().collection("Users")
                .document(currentUser.getUid());

        final CollectionReference collectionReference =  FirebaseFirestore.getInstance().collection("Users");

        // When there is a change in current user's data
        docReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.d("Error: ", error.getMessage());
                    return;
                }

                if(value == null)
                    return;

                // List the user's friends
                List<String> userIdList = (List<String>) value.get("friends");
                userIdList.add(currentUser.getUid());

                if(userIdList == null || userIdList.isEmpty())
                    return;
                // For every user in the friend list
                collectionReference.whereIn("uid", userIdList)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                               if(error != null){
                                   Log.d("Error: ", error.getMessage());
                               }

                               if(value == null)
                                   return;
                               // for every user's snapshot
                                for(QueryDocumentSnapshot snapshot : value){
                                    snapshot.getReference().collection("Post Images")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                    if (error != null) {
                                                        Log.d("Error: ", error.getMessage());
                                                     }
                                                    if (value == null) {
                                                        return;
                                                    }
                                                    //for every user's post
                                                    for (QueryDocumentSnapshot snapshot : value) {

                                                        if (!snapshot.exists()) {
                                                            return;
                                                        }
                                                        // Do it only one time
                                                        if (image == 0) {

                                                            HomePageModel homePageModel = snapshot.toObject(HomePageModel.class);
                                                            // Check if the post is posted today
                                                            if (postedToday(homePageModel.getTime(), currentDate)) {
                                                                // Load post's and user's data
                                                                textView.setVisibility(View.GONE);
                                                                recyclerView.setVisibility(View.VISIBLE);
                                                                homePageModelList.add(new HomePageModel(
                                                                        homePageModel.getUsername(),
                                                                        homePageModel.getProfImage(),
                                                                        homePageModel.getImageUrl(),
                                                                        homePageModel.getUid(),
                                                                        homePageModel.getDescription(),
                                                                        homePageModel.getId(),
                                                                        homePageModel.getTime(),
                                                                        homePageModel.getCongratsCounter()
                                                                ));
                                                            }
                                                            homePageAdapter.notifyDataSetChanged();
                                                        }
                                                }
                                                }
                                            });
                                }
                            }
                        });
            }
        });


    }
    // Initialize elements
    private void init(@NonNull View view){

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if(getActivity() != null){
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        recyclerView = view.findViewById(R.id.recycler_view);
        textView = view.findViewById(R.id.text);
        textView.setVisibility(View.VISIBLE);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }
    // Create notification when someone likes the post
    void createNotification(String userUid, String image_id){
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notification");

        String id = reference.document().getId();
        // Load notification's data
        Map<String, Object> map = new HashMap<>();
        map.put("time", FieldValue.serverTimestamp());
        map.put("notification", currentUser.getDisplayName() + " liked your photo");
        map.put("id", id);
        map.put("image", image_id);
        map.put("uid", userUid);

        reference.document(id).set(map);
    }

    // Function that tests if the post is posted today
    private boolean postedToday(Date imageDate, Date currentDate) {
        if(imageDate!=null && currentDate != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(imageDate).equals(simpleDateFormat.format(currentDate));
        }

        else
            return false;


    }
}