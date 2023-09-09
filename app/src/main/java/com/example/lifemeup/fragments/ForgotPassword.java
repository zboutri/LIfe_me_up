package com.example.lifemeup.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifemeup.R;
import com.example.lifemeup.ReplaceActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends Fragment {
    private EditText email;
    private Button  recover;
    private TextView login;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    public static final String EMAIL_REGEX = "^(.+)@(\\S+)$";

    public ForgotPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recover = view.findViewById(R.id.recover);
        email = view.findViewById(R.id.email);
        login = view.findViewById(R.id.login);
        progressBar = view.findViewById(R.id.progress_bar);
        auth = FirebaseAuth.getInstance();
        listeners();
    }

    private void listeners(){

        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_add = email.getText().toString();

                if(email_add.isEmpty() || !email_add.matches(EMAIL_REGEX)){
                    email.setError("The email address isn't correct");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                // Send the email to recover the password
                auth.sendPasswordResetEmail(email_add)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // If it is successful show this note
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(), "Password reset email send successfully", Toast.LENGTH_SHORT).show();
                                    email.setText("");
                                    progressBar.setVisibility(View.GONE);
                                }
                                else{
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(getContext(), "Error: "+errorMessage, Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });
        // When login button is clicked change go to Login fragment
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReplaceActivity) getActivity()).setFragment(new LoginFragment());
            }
        });
    }
}