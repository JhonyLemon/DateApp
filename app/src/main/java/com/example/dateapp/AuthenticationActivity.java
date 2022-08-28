package com.example.dateapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dateapp.databinding.AuthenticationActivityBinding;
import com.example.dateapp.models.UserData;
import com.example.dateapp.viewmodels.AuthenticationViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;


public class AuthenticationActivity extends AppCompatActivity {

    //binding to view
    private AuthenticationActivityBinding binding;
    // variable for Firebase Auth


    private AuthenticationViewModel authenticationViewModel;

    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AuthenticationActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authenticationViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        authenticationViewModel.init();


        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setTitle("");
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(binding.navHostFragment.getId());
        navController = navHostFragment.getNavController();

        authenticationViewModel.getFragment().observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null)
                    binding.linearProgressIndicator.setProgress(integer.intValue());
            }
        });

    }

    public void SetSupportActionBarVisibility(int id)
    {
        if(id== View.GONE){
            getSupportActionBar().hide();
        }
        else if(id==View.VISIBLE){
            getSupportActionBar().show();
        }
    }

    public void SetProgressBarVisibility(int id) {
        binding.linearProgressIndicator.setVisibility(id);
    }
}