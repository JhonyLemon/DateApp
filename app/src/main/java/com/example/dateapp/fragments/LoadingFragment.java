package com.example.dateapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.dateapp.AuthenticationActivity;
import com.example.dateapp.MainActivity;
import com.example.dateapp.R;
import com.example.dateapp.databinding.FragmentLoadingBinding;
import com.example.dateapp.models.UserData;
import com.example.dateapp.models.UserDetails;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class LoadingFragment extends Fragment {

    public static final String TAG="LoadingFragment";

    private FragmentLoadingBinding binding;

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private Boolean isNewUser=true;
    private NavController navController;
    NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.loadingFragment,true).build();


    public LoadingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        // sign out method from Firebase.
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user=mAuth.getCurrentUser();
        if(getActivity() instanceof AuthenticationActivity)
        {
           // ((AuthenticationActivity)getActivity()).SetProgressBarVisibility(View.GONE);
           // ((AuthenticationActivity)getActivity()).SetSupportActionBarVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController=Navigation.findNavController(view);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    private void CheckIfUser(){
        if(user!=null) {

            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        GetUserInfo();
                    } else {
                        user = null;
                        isSignedIn();
                    }
                }
            });
        }
        else{
            isSignedIn();
        }
    }

    private void userSignedIn() {
        //checking if email isnt null
        if(user.getEmail()!=null)
        {
            //checking if email is verified
            if(!user.isEmailVerified())
            {
                navController.navigate(R.id.enterVerificationFragment,null,navOptions);
            }
            //checking if user is new
            else if(isNewUser){
                navController.navigate(R.id.enterNameFragment,null,navOptions);
            }
            else{
                moveToMainActivity();
            }
        }
        else
        {
            if(isNewUser){
                navController.navigate(R.id.enterNameFragment,null,navOptions);
            }
            else{
                moveToMainActivity();
            }
        }
    }

    public void isSignedIn() {
        //checking if user is logged in
        if (user != null) {
            userSignedIn();
        }
        else{
            navController.navigate(R.id.authPickerFragment,null,navOptions);
        }
    }

    private void GetUserInfo()
    {
        mDatabase.child("users").child(user.getUid()).child("isNewUser").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    isNewUser= (Boolean) task.getResult().getValue();
                    Log.e(TAG, "Error getting data", task.getException());
                }
                else {
                    if(task.getResult().getValue()==null)
                        isNewUser=true;
                    Log.d(TAG, String.valueOf(task.getResult().getValue()));
                }
                isSignedIn();
            }
        });

    }

    private void moveToMainActivity()
    {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        CheckIfUser();
    }
}
