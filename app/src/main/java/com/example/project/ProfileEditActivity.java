package com.example.project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project.databinding.ActivityProfileEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileEditActivity extends AppCompatActivity {

    //view binding
    private ActivityProfileEditBinding binding;

    //firebase auth,get/update user data using uid
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    private static final String TAG = "PROFILE_EDIT_TAG";

    private Uri imageUri = null;

    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //setup firebase auth
        firebaseAuth = firebaseAuth.getInstance();
        loadUserInfo();

        //handle click, goback
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageAttachMenu();
            }
        });

        //handle click, update profile
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateDate();
            }
        });
    }

    private void loadUserInfo(){
        Log.d(TAG,"loadUserInfo: Loading user info...."+firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info of user here from snapshot
                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String userType = ""+snapshot.child("userType").getValue();


                        //set data to ui
                        binding.nameEt.setText(name);

                        //set image, using glide
                        Glide.with(ProfileEditActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person_grey)
                                .into(binding.profileIv);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void validateDate(){
        //get data
        name = binding.nameEt.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(name)){
            //no name entered
            Toast.makeText(this,"Enter name...",Toast.LENGTH_SHORT).show();

        }
        else{
            //name is entered
            if(imageUri == null){
                //need to update without image
                updateProfile("");
            }
            else{
                //need to update with image
                uploadImage();
            }
        }
    }

    private void uploadImage(){
        Log.d(TAG,"UploadImage: Uploading profile image... ");
        progressDialog.setMessage("Updating profile image");
        progressDialog.show();

        //image path and name,useuid to replace previous
        String filePathAndName = "ProfileImage/"+firebaseAuth.getUid();

        //storage reference
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"onSuccess: Profile image uploaded");
                        Log.d(TAG,"onSuccess: Getting url of uploaded image");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedImageUrl = ""+uriTask.getResult();

                        Log.d(TAG,"onSuccess: Uploaded Image URL: "+uploadedImageUrl);

                        updateProfile(uploadedImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: Failed to upload image due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Failed to upload image due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfile(String imageUrl){
        Log.d(TAG,"updateProfile: Updateing user profile");
        progressDialog.setMessage("Updating user profile...");
        progressDialog.show();

        //setup data to update in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", ""+name);
        if (imageUri != null){
            hashMap.put("profileImage", ""+imageUrl);

        }

        //update data to db
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Profile updated...");
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Profile updated...",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: Failed to update due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Failed to update due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImageAttachMenu(){
        //init/setup popup menu
        PopupMenu popupMenu = new PopupMenu(this,binding.profileIv);
        popupMenu.getMenu().add(Menu.NONE,0,0,"Camera");
        popupMenu.getMenu().add(Menu.NONE,1,1,"Gallery");

        popupMenu.show();

        //handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //get id of item clicked
                int which = menuItem.getItemId();
                if (which == 0){
                    //camera clicked
                    pickImageCamera();
                }
                else if (which == 1){
                    //gallery clicked
                    pickImageGallery();
                }

                return false;
            }
        });
    }

    private void pickImageCamera(){
        //intent to pick image from camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pick");//image title
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Image Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private void pickImageGallery(){
        //intent to pick image from camera
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of camera intent
                    //get url of image
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG,"onActivityResult: Picked from Camera "+imageUri);
                        Intent data = result.getData();

                        binding.profileIv.setImageURI(imageUri);

                    }
                    else{
                        Toast.makeText(ProfileEditActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of gallery intent
                    //get url of image
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG,"onActivityResult: "+imageUri);
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG,"onActivityResult: Picked from gallery "+imageUri);

                        binding.profileIv.setImageURI(imageUri);

                    }
                    else{
                        Toast.makeText(ProfileEditActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

}