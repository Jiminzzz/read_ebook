package com.example.project.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Start extends AppCompatActivity {

   private FirebaseAuth firebaseAuth;
   private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_start);

       firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        },2000);
    }

    private void checkUser(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            startActivity(new Intent(Start.this,MainActivity.class));
            finish();
        }
        else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            //progressDialog.dismiss();
                            String userType = ""+snapshot.child("userType").getValue();
                            if(userType.equals("user")){
                                startActivity(new Intent(Start.this,DashboardActivity2.class));
                                finish();
                            }
                            else if(userType.equals("admin")){
                                startActivity(new Intent(Start.this,DashboardActivity.class));
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled( DatabaseError error) {

                        }
                    });
        }
    }

    public void Start(View v){
        Intent itn = new Intent(this, MainActivity.class);
        startActivity(itn);
    }

}