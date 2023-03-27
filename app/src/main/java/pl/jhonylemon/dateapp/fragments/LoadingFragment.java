package pl.jhonylemon.dateapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import pl.jhonylemon.dateapp.MainActivity;
import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentLoadingBinding;
import pl.jhonylemon.dateapp.entity.UserData;
import pl.jhonylemon.dateapp.utils.AuthChecker;
import pl.jhonylemon.dateapp.utils.DataTransfer;

import com.google.firebase.auth.FirebaseUser;

public class LoadingFragment extends BaseFragment {

    public static final String TAG="LoadingFragment";

    private FragmentLoadingBinding binding;
    private FirebaseUser user;
    private NavController navController;

    private final AuthChecker authChecker = new AuthChecker();
    private final DataTransfer dataTransfer = new DataTransfer();
    private final NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.loadingFragment,true).build();


    public LoadingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        return this.init(binding);
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

    private void moveToAuthPicker(){
        navController.navigate(R.id.authPickerFragment,null,navOptions);
    }
    private void moveToAccountCreation(){
        dataTransfer.setUUID(dataTransfer.getUUID());
        navController.navigate(R.id.enterNameFragment,null,navOptions);
    }
    private void moveToMainActivity(){
        this.mainActivityViewModel.setBottomBarVisible(true);
        this.mainActivityViewModel.setActionBarVisible(true);
        this.mainActivityViewModel.setProgressBarVisible(false);
        navController.navigate(R.id.swipeFragment,null,navOptions);
    }

    private void checkState(){
        if(!authChecker.checkUser()){//no user found
            moveToAuthPicker();//move to Auth Picker Fragment
        }else {
            authChecker.reloadUser().addOnCompleteListener(authTask -> {// reloading user data to make sure it wasn't deleted
                if(authTask.isSuccessful()){//reload successful
                    if(authTask.getResult()!=null){
                        user=authTask.getResult();
                        dataTransfer.getNewUser(user.getUid()).get().addOnCompleteListener(userDataTask -> {
                            if(userDataTask.isSuccessful()){//retrieving user data successful
                                if(Boolean.FALSE.equals(userDataTask.getResult().getValue(Boolean.class))){
                                    mainActivityViewModel.setEnableLocation(true);
                                    mainActivityViewModel.getMainActivity().startGps();
                                    this.mainActivityViewModel.getMainActivity().addProfilePhotoListener();
                                    moveToMainActivity();//moving to main Fragment of application
                                }else{//user account is new, move to profile creation
                                    dataTransfer.setNewUser(user.getUid(),true);
                                    moveToAccountCreation();
                                }
                            }else {//failed to retrieve user data
                                dataTransfer.setNewUser(user.getUid(),true);
                                moveToAccountCreation();
                            }
                        });
                    }else{//reloaded user empty
                        moveToAuthPicker();
                    }
                }else{//failed to reload user
                    moveToAuthPicker();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkState();
    }
}
