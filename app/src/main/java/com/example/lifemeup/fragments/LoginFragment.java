package com.example.lifemeup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lifemeup.MainActivity;
import com.example.lifemeup.R;
import com.example.lifemeup.ReplaceActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
//import java.util.HashMap;
//import java.util.Map;


public class LoginFragment extends Fragment {
    private EditText email, password;
    private Button login ;
    private TextView forgot_password, signUp;
    private FirebaseAuth auth;

    private ProgressBar progressBar;
    public static final String EMAIL_REGEX = "^(.+)@(\\S+)$";


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_log_in_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);



        clickListener();
    }

    private void clickListener() {

        forgot_password.setOnClickListener((v) -> ((ReplaceActivity) requireActivity()).setFragment(new ForgotPassword()));

        signUp.setOnClickListener((v) -> ((ReplaceActivity) requireActivity()).setFragment(new SignUpFragment()));

        login.setOnClickListener((v) -> {
            String email_add = email.getText().toString();
            String code = password.getText().toString();


            if(email_add.isEmpty() || !email_add.matches(EMAIL_REGEX)){
                email.setError("Please input a valid email");
                return;
            }
            if(code.isEmpty() || code.length() < 8 ){
                password.setError("Please input a valid password");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email_add, code).
                    addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;
                            if (!user.isEmailVerified()) {
                                Toast.makeText(getContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }else
                                sendUserToMain();



                        } else {
                            progressBar.setVisibility(View.GONE);
                            String exception = Objects.requireNonNull(task.getException()).getMessage();
                            Toast.makeText(getContext(), "error" + exception, Toast.LENGTH_SHORT).show();
                        }
                    });
        });



    }

    private void sendUserToMain(){

        if(getActivity() == null){
            return;
        }

        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
        getActivity().finish();
    }



    private void init(View view) {

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        login = view.findViewById(R.id.log_in_btn);
        signUp = view.findViewById(R.id.signup);
        ImageView logo = view.findViewById(R.id.logo);
        progressBar = view.findViewById(R.id.progress_bar);

        forgot_password = view.findViewById(R.id.forgot_pass);
        auth = FirebaseAuth.getInstance();

    }
}