package com.example.lifemeup.fragments;

//imports
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
//import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lifemeup.R;
/*import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;*/
import com.example.lifemeup.ReplaceActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SignUpFragment extends Fragment {

    // Declare instance variables
    private EditText username, email, password, confirmation, goal;
    private Button login, create_account;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    // Regex pattern for email validation
    public static final String EMAIL_REGEX = "(.+)@(\\S+)$";

    // Default constructor
    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
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

        // Click listener for the login button
        login.setOnClickListener(v -> ((ReplaceActivity) getActivity()).setFragment(new LoginFragment()));

        // Click listener for the create_account button
        create_account.setOnClickListener(v -> {
            // Retrieve input values
            String name = username.getText().toString();
            String email_add = email.getText().toString();
            String code = password.getText().toString();
            String code_ver = confirmation.getText().toString();
            String status = goal.getText().toString();

            // Input validation
            if (name.isEmpty() || name.equals(" ")) {
                username.setError("Please input a valid name");
                return;
            }
            if (email_add.isEmpty() || !email_add.matches(EMAIL_REGEX)) {
                email.setError("Please input a valid email");
                return;
            }
            if (code.isEmpty() || code.length() < 8) {
                password.setError("Please input a valid password");
                return;
            }
            if (!code_ver.equals(code)) {
                confirmation.setError("Password doesn't match");
                return;
            }
            if (status.isEmpty() || status.equals(" ")) {
                goal.setError("Please input a goal");
                return;
            }

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Create user account
            createAccount(name, email_add, code, status);
        });

    }

    private void createAccount(String name, String email_add, String code, String status) {

        // Create user account with email and password
        auth.createUserWithEmailAndPassword(email_add, code).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                String image = "https://www.shutterstock.com/image-vector/people-icon-vector-person-sing-user-707883430";
                UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                request.setDisplayName(name);
                request.setPhotoUri(Uri.parse(image));
                assert user != null;
                user.updateProfile(request.build());

                // Send email verification
                user.sendEmailVerification().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(getContext(), "Email verification link sent", Toast.LENGTH_SHORT).show();
                    }
                });

                // Upload user information to Firestore
                uploadUser(user, name, email_add, status);
            } else {
                progressBar.setVisibility(View.GONE);
                String exception = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(getContext(), "Error: " + exception, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadUser(FirebaseUser user, String name, String email, String status) {

        List<String> list = new ArrayList<>();
        // Create a map with user information
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("image_prof", " ");
        map.put("uid", user.getUid());
        map.put("friends",  list);
        map.put("status", status);
        map.put("search", name.toLowerCase());

        // Save user information to Firestore collection "Users"
        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        Intent intent = new Intent(getContext(), ReplaceActivity.class);
                        intent.putExtra("isComment", false);
                        intent.putExtra("newGoal", false);

                        getContext().startActivity(intent);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String exception = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(getContext(), "Error: " + exception, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init(View view) {

        // Initialize views
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        confirmation = view.findViewById(R.id.confirmation);
        login = view.findViewById(R.id.login);
        create_account = view.findViewById(R.id.create_account);
        progressBar = view.findViewById(R.id.progress_bar);
        goal = view.findViewById(R.id.goal);
        auth = FirebaseAuth.getInstance();

    }
}
