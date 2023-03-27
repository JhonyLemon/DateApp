package pl.jhonylemon.dateapp.receiver;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.location.LocationListenerCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.location.LocationRequestCompat;
import androidx.lifecycle.ViewModelProvider;

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

import java.util.List;
import java.util.Map;

import pl.jhonylemon.dateapp.MainActivity;
import pl.jhonylemon.dateapp.entity.UserLocation;
import pl.jhonylemon.dateapp.utils.DataTransfer;
import pl.jhonylemon.dateapp.utils.GpsChecker;
import pl.jhonylemon.dateapp.viewmodels.MainActivityViewModel;

public class GpsChangeReceiver extends BroadcastReceiver {

    private static final int REQUEST_CHECK_SETTINGS = 111;
    private LocationManager locationManager;
    private LocationListenerCompat locationListenerCompat;
    private LocationRequestCompat locationRequestCompat;
    private Context context;
    private MainActivityViewModel mainActivityViewModel;
    private DataTransfer dataTransfer;

    private ActivityResultCallback<Map<String, Boolean>> gpaPermissions;
    private ActivityResultLauncher<String[]> gpsPermissionResult;

    public ActivityResultCallback<Map<String, Boolean>> getGpaPermissions() {
        return gpaPermissions;
    }

    public GpsChangeReceiver(Context context, MainActivityViewModel viewModel) {
        this.context = context;
        this.mainActivityViewModel = viewModel;
        this.dataTransfer = new DataTransfer();
        gpaPermissions = result -> {
            if (!GpsChecker.checkGpsPermission(context)) {
                askForGpsPermissions();
            }
        };

        //getting location manager
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        //listener for getting location
        locationListenerCompat = location -> {
            dataTransfer.setLocation(new UserLocation(
                    dataTransfer.getUUID(),
                    location.getLongitude(),
                    location.getLatitude(),
                    GeoFireUtils.getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()))
            ));
        };

        //location request (how often should location be updated)
        locationRequestCompat = new LocationRequestCompat.Builder(1000)
                .setMinUpdateDistanceMeters(1000)
                .setQuality(LocationRequestCompat.QUALITY_LOW_POWER)
                .build();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)){
            if (!GpsChecker.checkIfLocationEnabled(context)) {
                startLocation();
            }
        }
    }

    public boolean startLocation() {
        if (mainActivityViewModel.getEnableLocation().getValue()) {
            askForGpsPermissions();
            askToEnableGps();
            List<String> providers = locationManager.getProviders(false);
            if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                requestLocationUpdates(LocationManager.NETWORK_PROVIDER);
                return true;
            }
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                requestLocationUpdates(LocationManager.GPS_PROVIDER);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void requestLocationUpdates(String provider) {
        if (GpsChecker.checkGpsPermission(context)) {
            askToEnableGps();
            LocationManagerCompat.requestLocationUpdates(
                    locationManager,
                    provider,
                    locationRequestCompat,
                    locationListenerCompat,
                    Looper.getMainLooper()
            );
        } else {
            askForGpsPermissions();
            requestLocationUpdates(provider);
        }
    }

    private void askForGpsPermissions() {
        if (!GpsChecker.checkGpsPermission(context)) {
            gpsPermissionResult.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void askToEnableGps() {
        if (!GpsChecker.checkIfLocationEnabled(context)) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(Priority.PRIORITY_LOW_POWER);
            locationRequest.setSmallestDisplacement(1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true); //this displays dialog box like Google Maps with two buttons - OK and NO,THANKS

            Task<LocationSettingsResponse> task =
                    LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());

            task.addOnCompleteListener(task1 -> {
                try {
                    LocationSettingsResponse response = task1.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        mainActivityViewModel.getMainActivity(),
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    public void setGpsPermissionResult(ActivityResultLauncher<String[]> gpsPermissionResult) {
        this.gpsPermissionResult = gpsPermissionResult;
    }
}
