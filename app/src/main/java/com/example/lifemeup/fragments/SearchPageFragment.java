package com.example.lifemeup.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lifemeup.R;
import com.example.lifemeup.addapter.UserAdapter;
import com.example.lifemeup.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SearchPageFragment extends Fragment {

    SearchView searchBar;
    RecyclerView recyclerView;
    CollectionReference collectionReference;
    UserAdapter userAdapter;
    private List<UserModel> userModelList;

    OnProfileChange onProfileChange;

    // New interface in order to open the searched profile
    public interface OnProfileChange{
        void profileChange(String uid);
    }

    // Pass the context
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onProfileChange = (OnProfileChange) context;
    }

    public SearchPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        // Get access to users collection in firebase
        collectionReference = FirebaseFirestore.getInstance().collection("Users");

        loadUserData();

        searchUser();
        clickListener();

    }

    private void clickListener(){
        // When a profile is selected pass user's data
        userAdapter.OnProfileClicked(new UserAdapter.OnProfileClicked() {
            @Override
            public void onClicked(String uid) {
                onProfileChange.profileChange(uid);
            }
        });
    }

    private void searchUser(){
        // When the user searches a name in the search bar
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Load users that matches the search query
                collectionReference.orderBy("search").startAt(query).endAt(query+"\uf8ff")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    userModelList.clear();
                                    for(DocumentSnapshot snapshot: task.getResult()){
                                        if(!snapshot.exists()){
                                            return;
                                        }

                                        UserModel users = snapshot.toObject(UserModel.class);
                                        userModelList.add(users);
                                    }
                                    userAdapter.notifyDataSetChanged();
                                }
                            }
                        });

                return false;
            }
            // When the search bar is empty load all users
            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.equals("")) {
                    userModelList.clear();
                    loadUserData();
                }
                return false;
            }
        });


    }


    // Load to the userModelList all the users from firebase
    private void loadUserData(){

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null || value == null)
                    return;

                for(QueryDocumentSnapshot snapshot : value){
                   UserModel users = snapshot.toObject(UserModel.class);
                   userModelList.add(users);
               }
                // Notify adapter that data has changed
                userAdapter.notifyDataSetChanged();
            }
        });
    }


    // Initialize elements
    private void init(View view){

        searchBar = view.findViewById(R.id.searchBar);
        userModelList = new ArrayList<>();
        userAdapter = new UserAdapter(userModelList);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(userAdapter);
    }


}