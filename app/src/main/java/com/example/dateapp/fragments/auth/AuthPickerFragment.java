package com.example.dateapp.fragments.auth;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
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
import com.example.dateapp.R;
import com.example.dateapp.databinding.FragmentAuthPickerBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthPickerFragment extends Fragment implements ActivityResultCallback<FirebaseAuthUIAuthenticationResult>, View.OnClickListener {

    public static final String TAG="AuthPickerFragment";
    private FragmentAuthPickerBinding binding;
    NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.authPickerFragment,true).build();
    private NavController navController;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final String GOOGLE_PRIVACY_POLICY_URL = "https://www.google.com/policies/privacy/";
    private static final String FIREBASE_PRIVACY_POLICY_URL = "https://firebase.google.com/terms/analytics/#7_privacy";

    private final ActivityResultLauncher<Intent> signIn =
            registerForActivityResult(new FirebaseAuthUIActivityResultContract(), this);

    Map<Integer,AuthUI.IdpConfig> providers = new HashMap();

    public AuthPickerFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthPickerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        providers.put(binding.customEmailSigninButton.getId(),
                new AuthUI.IdpConfig.EmailBuilder()
                .setRequireName(false)
                .setAllowNewAccounts(true)
                .build());
        providers.put(binding.customGoogleSigninButton.getId(),
                new AuthUI.IdpConfig.GoogleBuilder()
                        .build());
        binding.customGoogleSigninButton.setOnClickListener(this);
        binding.customEmailSigninButton.setOnClickListener(this);
        if(getActivity() instanceof AuthenticationActivity)
        {
           // ((AuthenticationActivity)getActivity()).SetProgressBarVisibility(View.GONE);
           // ((AuthenticationActivity)getActivity()).SetSupportActionBarVisibility(View.GONE);
        }

        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    public Intent getIntent(int id)
    {
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                // smart lock
                .setIsSmartLockEnabled(false,true)

                // providers we can sign in with
                .setAvailableProviders(Collections.singletonList(providers.get(id)))
                //setting theme for intent
                .setTosAndPrivacyPolicyUrls(FIREBASE_TOS_URL,FIREBASE_PRIVACY_POLICY_URL)
                // creating custom singing intent
                .build();
    }

    @Override
    public void onActivityResult(@NonNull FirebaseAuthUIAuthenticationResult result) {
        //result of intent
        IdpResponse response = result.getIdpResponse();
        //response to result
        handleSignIn(result.getResultCode(), response);
    }

    private void handleSignIn(int resultCode, @Nullable IdpResponse response) {
        // Successfully signed in
        if (resultCode == RESULT_OK) {
            //logged in
            user=mAuth.getCurrentUser();
            userSignedIn();
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                return;
            }
            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {

                showSnackbar(R.string.no_internet_connection);
                return;
            }
            if (response.getError().getErrorCode() == ErrorCodes.ERROR_USER_DISABLED) {
                showSnackbar(R.string.account_disabled);
                return;
            }
            showSnackbar(R.string.unknown_error);
            Log.e(TAG, "Sign-in error: ", response.getError());
        }
    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(binding.getRoot(), errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        signIn.launch(getIntent(v.getId()));
    }

    private void userSignedIn() {
        //checking if email isn't null
        if(user.getEmail()!=null)
        {
            //checking if email is verified
            if(!user.isEmailVerified())
            {
                navController.navigate(R.id.enterVerificationFragment,null,navOptions);
            }
            else{
                navController.navigate(R.id.enterNameFragment,null,navOptions);
            }
        }
        else
        {
            navController.navigate(R.id.enterNameFragment,null,navOptions);
        }
    }


}
