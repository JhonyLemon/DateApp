package com.example.dateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.dateapp.databinding.ActivityMainBinding;
import com.example.dateapp.fragments.ChatFragment;
import com.example.dateapp.fragments.LikedFragment;
import com.example.dateapp.fragments.SwipeFragment;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setTitle("");
        binding.title.setText(R.string.app_name);
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(binding.navHostFragment.getId());
        NavController navController = navHostFragment.getNavController();
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                binding.topAppBar.setVisibility(View.GONE);
                binding.bottomNavigation.setVisibility(View.GONE);
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.swipeFragment:
                        selectedFragment = new SwipeFragment();
                        break;
                    case R.id.chatFragment:
                        selectedFragment = new ChatFragment();
                        break;
                    case R.id.likedFragment:
                        selectedFragment = new LikedFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(binding.navHostFragment.getId(),
                        selectedFragment).commit();
                return true;
            }
        });

        binding.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}