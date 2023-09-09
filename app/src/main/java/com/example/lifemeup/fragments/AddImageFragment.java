package com.example.lifemeup.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lifemeup.MainActivity;
import com.example.lifemeup.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddImageFragment extends Fragment {

    private EditText description;
    private ImageView imageView;
    //private RecyclerView recyclerView;


    private ImageButton backBtn, nextBtn, add_image;
    private FirebaseUser user;
    ActivityResultLauncher<Intent>  activityResultLauncher;;
    Uri imageUri;
    Dialog dialog;


    public AddImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        CropImage.ActivityResult crop_result = CropImage.getActivityResult(result.getData());


                        Uri uri = crop_result.getUri();
                        imageUri = uri;

                        Glide.with(getContext())
                                .load(uri)
                                .into(imageView);
                        imageView.setVisibility(View.VISIBLE);

                        nextBtn.setVisibility(View.VISIBLE);
                        add_image.setVisibility(View.GONE);
                    }
                });


        clickListener();
    }

    private void clickListener(){


        nextBtn.setOnClickListener(v -> {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("Post Images/" + System.currentTimeMillis());

            dialog.show();
            storageReference.putFile(imageUri)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> uploadData(uri.toString()));
                        }
                        else{
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Failed to upload post", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                getActivity().finish();
            }
        });


    }



    private void uploadData(String imageUrl){

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid()).collection("Post Images");


        String id = reference.document().getId();
        String desc = description.getText().toString();

        List<String> list = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("description", desc);
        map.put("time", FieldValue.serverTimestamp());
        map.put("imageUrl", imageUrl);

        map.put("username", user.getDisplayName());
        map.put("profImage", String.valueOf(user.getPhotoUrl()));
        map.put("congratsCounter", list);
        map.put("comments", list);
        map.put("uid", user.getUid());

        reference.document(id).set(map)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        System.out.println();
                        Toast.makeText(getContext(), "Uploaded",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "Error: "+task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
    }

    private void init(View view){

        description = view.findViewById(R.id.description);
        imageView = view.findViewById(R.id.imageView);
      //  recyclerView = view.findViewById(R.id.recycler_view);
        add_image = view.findViewById(R.id.add_photo_btn);
        nextBtn = view.findViewById(R.id.nextBtn);
        backBtn = view.findViewById(R.id.backBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dialog_bg, null));
        dialog.setCancelable(false);
    }


    private void requestPermission() {


        Dexter.withContext(getContext())
                .withPermissions(Manifest.permission.CAMERA )
                .withListener(new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            CropImage.ActivityBuilder cropBuilder = CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setAspectRatio(1, 1);

                            // Start the crop activity using the ActivityResultLauncher
                            activityResultLauncher.launch(cropBuilder.getIntent(getContext()));



                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permanently, we will show user a dialog message.
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
    }

    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel();
            // below is the intent from which we are redirecting our user.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // this method is called when user click on negative button.
            dialog.cancel();
        });
        // below line is used to display our dialog
        builder.show();
    }

}
