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

import com.example.lifemeup.R;
import com.example.lifemeup.addapter.NotificationsAdapter;
import com.example.lifemeup.model.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class NotificationsPageFragment extends Fragment {

    List<NotificationModel> notificationModelList;
    NotificationsAdapter notificationsAdapter;
    RecyclerView recyclerView;
    FirebaseUser currentUser;

    public NotificationsPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Initialize the list and adapter for notifications
        notificationModelList = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(notificationModelList, getContext());
        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(notificationsAdapter);

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Notification");

        // Get access to current user's notifications and sort them by time
        collectionReference.whereEqualTo("uid", currentUser.getUid()).orderBy("time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null || value.isEmpty())
                    return;
                // Process each notification and add it to the list
                for(QueryDocumentSnapshot snapshot : value){
                    NotificationModel notificationModel = snapshot.toObject(NotificationModel.class);
                    notificationModelList.add(notificationModel);
                }
                notificationsAdapter.notifyDataSetChanged();
            }
        });

    }
}