package com.example.lifemeup;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.lifemeup.fragments.AddImageFragment;
import com.example.lifemeup.fragments.OpenImageFragment;
import com.example.lifemeup.fragments.NotificationsPageFragment;
import com.example.lifemeup.fragments.HomePageFragment;
import com.example.lifemeup.fragments.ProfilePageFragment;
import com.example.lifemeup.fragments.SearchPageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SearchPageFragment.OnProfileChange , ProfilePageFragment.OnImagePass {

    BottomNavigationView navigationView;
    private int tab = 0;
    public static String DESC, IMAGE_URL;
    public static Date TIME;
    public static String USER_ID;
    public static boolean IS_SEARCHED_USER = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    private void init(){

        // Initialize navigation view
        navigationView = findViewById(R.id.navigation);
        // First page is the home page
        HomePageFragment hFragment = new HomePageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, hFragment, "").commit();

        // When an item is selected from the navigation view, replace fragments
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    // Home page
                    case R.id.nav_home:

                        tab = 0;
                        HomePageFragment hFragment = new HomePageFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, hFragment, "").commit();


                        return true;
                    // Add image page
                    case R.id.nav_add:
                        tab = 1;
                        AddImageFragment addFragment = new AddImageFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, addFragment, "").commit();

                        return true;
                    // User's Profile page
                    case R.id.nav_profile:
                        tab = 2;
                        IS_SEARCHED_USER = false;
                        ProfilePageFragment profFragment = new ProfilePageFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, profFragment, "").commit();

                        return true;
                    // Notification page
                    case R.id.nav_notify:
                        tab = 3;
                        NotificationsPageFragment notFragment = new NotificationsPageFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, notFragment, "").commit();

                        return true;
                    // Search page
                    case R.id.nav_search:
                        tab = 4;
                        SearchPageFragment sFragment = new SearchPageFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, sFragment, "").commit();

                        return true;
                }

                return false;
            }

        });

    }


    // When the onCheck is called from profile page fragment
    // in order to open a image to a new window
    public void onCheck(String imageUrl,String description, String uid, Date timestamp){
        IMAGE_URL = imageUrl;
        DESC = description;
        TIME = timestamp;
        USER_ID = uid;

        OpenImageFragment fragmentIm = new OpenImageFragment();
        FragmentTransaction fragmentImTransaction = getSupportFragmentManager().beginTransaction();
        fragmentImTransaction.replace(R.id.content, fragmentIm);
        fragmentImTransaction.commit();
    }


    // When onChange is called fro search page
    // in order to open an other user's profile
    public void profileChange(String uid) {
        USER_ID = uid;
        IS_SEARCHED_USER = true;

        ProfilePageFragment fragment1 = new ProfilePageFragment();
        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.content, fragment1);
        fragmentTransaction1.commit();
    }

    // When the back btn is pressed change fragment according
    // to which tab you are now
    @Override
    public void onBackPressed() {

        if(tab == 0){
            super.onBackPressed();
        }
        else{
            if (IS_SEARCHED_USER) {
                IS_SEARCHED_USER = false;

            }
            HomePageFragment fragment = new HomePageFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content, fragment, "");
            fragmentTransaction.commit();

            tab = 0;
        }

    }
}