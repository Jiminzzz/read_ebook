package com.example.project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( MainActivity.this ,LoginActivity.class));
            }
        } );

        binding.skipBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity( new Intent( MainActivity.this ,DashboardActivity2.class));
            }
        } );

    }

}