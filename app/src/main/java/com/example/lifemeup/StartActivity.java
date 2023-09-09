package com.example.lifemeup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//it appears the logo and the name of the app
//if the user exists goes to main activity
//otherwise to fragment_replacer
@SuppressLint("CustomSplashScreen")
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser curr_user = auth.getCurrentUser();

        new Handler().postDelayed(() -> {
            if(curr_user == null){
                startActivity(new Intent(StartActivity.this, ReplaceActivity.class));
            }
            else{
                startActivity(new Intent(StartActivity.this, MainActivity.class));
            }

            finish();
        }, 2500);
    }
}