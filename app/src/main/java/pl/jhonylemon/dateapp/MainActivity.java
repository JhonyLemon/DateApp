package pl.jhonylemon.dateapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.location.LocationListenerCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.location.LocationRequestCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import pl.jhonylemon.dateapp.databinding.MainActivityBinding;
import pl.jhonylemon.dateapp.entity.UserLocation;
import pl.jhonylemon.dateapp.fragments.main.MainFragment;
import pl.jhonylemon.dateapp.receiver.GpsChangeReceiver;
import pl.jhonylemon.dateapp.utils.DataTransfer;
import pl.jhonylemon.dateapp.utils.GpsChecker;
import pl.jhonylemon.dateapp.utils.PhotoTransfer;
import pl.jhonylemon.dateapp.viewmodels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MainActivity";
    private static final int REQUEST_CHECK_SETTINGS = 111;

    private MainActivityBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private NavController navController;
    private NavHostFragment navHostFragment;

    private GpsChangeReceiver gpsChangeReceiver;

    private DataTransfer dataTransfer;
    private PhotoTransfer photoTransfer;

    private ValueEventListener profilePhoto;

    private ActivityResultLauncher<String[]> gpsPermissionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        dataTransfer = new DataTransfer();
        photoTransfer = new PhotoTransfer();
        //initializing view model for activity
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.init();

        gpsChangeReceiver = new GpsChangeReceiver(getApplicationContext(),mainActivityViewModel);

        gpsPermissionResult=registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                gpsChangeReceiver.getGpaPermissions()
        );
        gpsChangeReceiver.setGpsPermissionResult(gpsPermissionResult);

        getApplicationContext().registerReceiver(gpsChangeReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(binding.navHostFragment
                                .getId());

        navController = navHostFragment
                .getNavController();

        mainActivityViewModel.getFragment().observeForever(integer -> {
            if (integer != null)
                binding.linearProgressIndicator.setProgress(integer.intValue());
        });
        mainActivityViewModel.getActionBarVisible().observeForever(visible -> {
            if (visible) {
                getSupportActionBar().show();
            } else {
                getSupportActionBar().hide();
            }
        });
        mainActivityViewModel.getProgressBarVisible().observeForever(visible -> {
            if (visible) {
                binding.linearProgressIndicator.setVisibility(View.VISIBLE);
            } else {
                binding.linearProgressIndicator.setVisibility(View.GONE);
            }
        });
        mainActivityViewModel.getBottomBarVisible().observeForever(visible -> {
            if (visible) {
                binding.bottomNavigation.setVisibility(View.VISIBLE);
            } else {
                binding.bottomNavigation.setVisibility(View.GONE);
            }
        });

        setContentView(binding.getRoot());
        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setTitle("");
        binding.title.setText(R.string.app_name);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Integer id = null;
            switch (item.getItemId()) {
                case R.id.swipeFragmentMenu:
                    id = R.id.swipeFragment;
                    break;
                case R.id.chatFragmentMenu:
                    id = R.id.chatFragment;
                    break;
            }
            if (id != null) {
                ((MainFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).move(id);
            }
            return true;
        });

        mainActivityViewModel.setActionBarVisible(false);
        mainActivityViewModel.setProgressBarVisible(false);
        mainActivityViewModel.setBottomBarVisible(false);


        binding.profilePicture.setOnClickListener(view -> {
            int id = navController.getCurrentBackStackEntry().getDestination().getId();
            if(mainActivityViewModel.getProfilePicture().getValue()==null){
                return;
            }
            if(id==R.id.profileFragment || id == R.id.profileEditFragment || id == R.id.settingsFragment){
                NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.profileFragment,true).build();
                navController.navigate(R.id.profileFragment,null,navOptions);
            }else{
                navController.navigate(R.id.profileFragment);
            }
        });
        mainActivityViewModel.setMainActivity(this);
    }

    public boolean startGps(){
        return gpsChangeReceiver.startLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS ) {
            if (resultCode == RESULT_OK) {
                //enabled
            } else {
                gpsChangeReceiver.startLocation();
            }
        }
    }

    public ValueEventListener addProfilePhotoListener(){

        return profilePhoto = dataTransfer.getUserPhotos(dataTransfer.getUUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uri = Optional.ofNullable((List<String>) snapshot.getValue()).orElse(new ArrayList<>()).stream().findFirst().orElse("");
                mainActivityViewModel.setProfilePicture(uri);
                Glide.with(getApplicationContext())
                        .load(uri)
                        .signature(new ObjectKey(uri))
                        .into(binding.profilePicture);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}