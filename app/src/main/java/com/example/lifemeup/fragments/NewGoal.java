package com.example.lifemeup.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lifemeup.MainActivity;
import com.example.lifemeup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewGoal extends Fragment {

    private EditText goal;
    private Button save, cancel;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ProgressBar progressBar;

    public NewGoal() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        init(view);

        // Set up click listeners
        clickListener();
    }

    private void clickListener() {

        save.setOnClickListener(v -> {
            // Retrieve input values

            String status = goal.getText().toString();

            // Input validation

            if (status.isEmpty() || status.equals(" ")) {
                goal.setError("Please input a goal");
                return;
            }

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);


            uploadUser(status);
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getActivity() != null;
                startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                getActivity().finish();
            }
        });

    }

    private void uploadUser(String status) {

        List<String> list = new ArrayList<>();
        // Create a map with user information
        Map<String, Object> map = new HashMap<>();

        map.put("status", status);

        // Save user information to Firestore collection "Users"
        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .update(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Start the main activity upon successful registration
                        assert getActivity() != null;
                        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                        progressBar.setVisibility(View.GONE);
                        getActivity().finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String exception = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(getContext(), "Error: " + exception, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init(View view) {

        // Initialize views

        save = view.findViewById(R.id.save);
        progressBar = view.findViewById(R.id.progress_bar);
        goal = view.findViewById(R.id.goal);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        cancel = view.findViewById(R.id.cancel);

    }
}