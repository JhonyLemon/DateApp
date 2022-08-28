package com.example.dateapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dateapp.databinding.ProfileActivityBinding;


public class ProfileActivity extends AppCompatActivity {

    private ProfileActivityBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProfileActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}