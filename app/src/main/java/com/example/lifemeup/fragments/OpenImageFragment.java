package com.example.lifemeup.fragments;

import static com.example.lifemeup.MainActivity.DESC;
import static com.example.lifemeup.MainActivity.IMAGE_URL;
import static com.example.lifemeup.MainActivity.TIME;
import static com.example.lifemeup.MainActivity.USER_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lifemeup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


public class OpenImageFragment extends Fragment {
    private  CircleImageView profImage;
    private  TextView name;
    private  TextView description;
    private  TextView hour;
    private  TextView likes_counter;
    private  ImageView imageView;
    private ImageButton backBtn;
    DocumentReference reference;

    public OpenImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize elements
        init(view);

        // Get access to a specific user's data
        reference = FirebaseFirestore.getInstance().collection("Users")
                .document(USER_ID);

        // Get data
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                // Load the image's data
                name.setText(task.getResult().get("name").toString());
                description.setText(DESC);
                Glide.with(getContext()).load(IMAGE_URL).into(imageView);
                Glide.with(getContext()).load(task.getResult().get("image_prof")).placeholder(R.drawable.user_60).timeout(6500).into(profImage);

                hour.setText(""+TIME);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the current fragment with the ProfilePageFragment
                FragmentTransaction fragmentTransaction1 = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction1.replace(R.id.content, new ProfilePageFragment());
                fragmentTransaction1.commit();
            }
        });

    }
    // Initialize elements
    private void init(View view){
        profImage = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.name);
        hour = view.findViewById(R.id.hour);
        likes_counter = view.findViewById(R.id.likes_counter);
        imageView = view.findViewById(R.id.posted_image);
        description = view.findViewById(R.id.desc);
        backBtn = view.findViewById(R.id.backBtn);
    }
}