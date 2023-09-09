package com.example.lifemeup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.lifemeup.fragments.CommentFragment;
import com.example.lifemeup.fragments.SignUpFragment;
import com.example.lifemeup.fragments.LoginFragment;
import com.example.lifemeup.fragments.NewGoal;

// A class helping to replace fragments
public class ReplaceActivity extends AppCompatActivity {

    private FrameLayout frame_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replacer);

        frame_layout = findViewById(R.id.frame_layout);

        // A flag that indicates to change to comments fragment
        boolean commentFlag = getIntent().getBooleanExtra("commentFlag", false);
        // A flag that indicates to change to NewGoal fragment
        boolean newGoal = getIntent().getBooleanExtra("newGoal", false);

        // According the flags change fragments
        if(newGoal){
            setFragment(new NewGoal());
        }
        else if(commentFlag){
            setFragment(new CommentFragment());
        }else{
            setFragment(new LoginFragment());

        }
    }
    // Make the transaction
    public void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if(fragment instanceof SignUpFragment){
            fragmentTransaction.addToBackStack(null);
        }
        if(fragment instanceof NewGoal){
            fragmentTransaction.addToBackStack(null);
        }

        // Pass the data needed for the comments page
        if(fragment instanceof CommentFragment){
            String id = getIntent().getStringExtra("id");
            String uid = getIntent().getStringExtra("userID");

            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("userID", uid);
            fragment.setArguments(bundle);
        }



        fragmentTransaction.replace(frame_layout.getId(), fragment);
        fragmentTransaction.commit();
    }
}