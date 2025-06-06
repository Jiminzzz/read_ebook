package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project.adapters.AdapterPdfFavorite;
import com.example.project.databinding.ActivityProfileBinding;
import com.example.project.models.ModelPdf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    //view binding
    private ActivityProfileBinding binding;

    //firebase auth, for loading user data using vid
    private FirebaseAuth firebaseAuth;

    //arrayList to hold the books
    private ArrayList<ModelPdf> pdfArrayList;
    //adapter to set in recyclerview
    private AdapterPdfFavorite adapterPdfFavorite;

    private static final String TAG = "PROFILE_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setup firebase auth
        firebaseAuth = firebaseAuth.getInstance();
        loadUserInfo();
        loadFavoriteBooks();

        //handle click, start profile edit page
        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,ProfileEditActivity.class));
            }
        });

        //handle click, goback
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

                        // format data to dd/MM/yyyy
                        String formattedData = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        //set data to ui
                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);
                        binding.memberDateTv.setText(formattedData);
                        binding.accountTypeTv.setText(userType);

                        //set image, using glide
                        Glide.with(ProfileActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person_grey)
                                .into(binding.profileIv);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void loadFavoriteBooks(){
        //init list
        pdfArrayList = new ArrayList<>();

        //load favorite books from database
        //User>userId>Favrites
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before starting adding data
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //we will only get the bookId here, and we got other details in adapter using that bookId
                            String bookId = ""+ds.child("bookId").getValue();

                            //set id to model
                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookId);

                            //add model to list
                            pdfArrayList.add(modelPdf);
                        }

                        //set number of favorite books
                        binding.favoriteBookCountTv.setText(""+pdfArrayList.size());
                        //swtup adapter
                        adapterPdfFavorite = new AdapterPdfFavorite(ProfileActivity.this, pdfArrayList);
                        //set adapter to recyclerview
                        binding.booksRv.setAdapter(adapterPdfFavorite);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}