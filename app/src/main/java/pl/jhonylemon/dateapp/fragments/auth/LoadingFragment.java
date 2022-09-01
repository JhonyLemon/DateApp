package pl.jhonylemon.dateapp.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import pl.jhonylemon.dateapp.MainActivity;
import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentLoadingBinding;
import pl.jhonylemon.dateapp.viewmodels.AuthenticationViewModel;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class LoadingFragment extends Fragment {

    public static final String TAG="LoadingFragment";

    private FragmentLoadingBinding binding;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private Boolean isNewUser=true;
    private NavController navController;
    private AuthenticationViewModel authenticationViewModel;
    NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.loadingFragment,true).build();


    public LoadingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        this.authenticationViewModel=new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        View view = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        // sign out method from Firebase.
        db = FirebaseFirestore.getInstance();
        user=mAuth.getCurrentUser();

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

            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    GetUserInfo();
                } else {
                    user = null;
                    isSignedIn();
                }
            });
        }
        else{
            isSignedIn();
        }
    }

    private void userSignedIn() {
        authenticationViewModel.setUserUID(user.getUid());
        //checking if email isnt null
        if(user.getEmail()!=null)
        {
            //checking if user is new
            if(isNewUser){
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
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Error getting data", task.getException());
                }
                else {
                    if(task.getResult()==null)
                        isNewUser=true;
                    Log.d(TAG,"Everything okay");
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
