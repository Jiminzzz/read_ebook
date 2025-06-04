package com.example.project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.adapters.AdapterPdfAdmin;
import com.example.project.databinding.ActivityPdfListAdminBinding;
import com.example.project.models.ModelPdf;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PdfListAdminActivity extends AppCompatActivity {
    //viewbinding
    private ActivityPdfListAdminBinding binding;
    //arraylist to hold list of data of type ModelPdf
    private ArrayList<ModelPdf>pdfArrayList;
    //adapter
    private AdapterPdfAdmin adapterPdfAdmin;
    private String categoryId,CategoryTitle;
    private static final String TAG = "PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding =ActivityPdfListAdminBinding.inflate( getLayoutInflater());
        setContentView(binding.getRoot());

        //get data for intent
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        CategoryTitle= intent.getStringExtra("categoryTitle");

        //set pdf category
        binding.subtitleTv.setText(CategoryTitle);

        loadPdfList();

        //search
        binding.searchEt.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //search as and when user type each letter
                try{
                    adapterPdfAdmin.getFilter().filter(charSequence);
                }
                catch (Exception e){
                    Log.d(TAG,"onTextChanged:"+e.getMessage());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        } );

    }

    private void loadPdfList() {
        //init list before adding data
        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo( categoryId )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            //get data
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            //add to list
                            pdfArrayList.add(model);

                            Log.d(TAG,"onDataChange:"+model.getId()+" "+model.getTitle());

                        }
                        // setup to list
                        adapterPdfAdmin = new AdapterPdfAdmin( PdfListAdminActivity.this,pdfArrayList);
                        binding.BookRv.setAdapter(adapterPdfAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );

    }

}