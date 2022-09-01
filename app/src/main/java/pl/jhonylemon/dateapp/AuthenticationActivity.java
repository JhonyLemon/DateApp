package pl.jhonylemon.dateapp;

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

import pl.jhonylemon.dateapp.databinding.AuthenticationActivityBinding;
import pl.jhonylemon.dateapp.models.UserData;
import pl.jhonylemon.dateapp.viewmodels.AuthenticationViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;


public class AuthenticationActivity extends AppCompatActivity {

    private AuthenticationActivityBinding binding;

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

        authenticationViewModel.getFragment().observeForever(integer -> {
            if(integer!=null)
                binding.linearProgressIndicator.setProgress(integer.intValue());
        });
        authenticationViewModel.getActionBarVisible().observeForever(visible->{
            if(visible){
                getSupportActionBar().show();
            }else{
                getSupportActionBar().hide();
            }
        });
        authenticationViewModel.getProgressBarVisible().observeForever(visible->{
            if(visible){
                binding.linearProgressIndicator.setVisibility(View.VISIBLE);
            }else{
                binding.linearProgressIndicator.setVisibility(View.GONE);
            }
        });

    }
}