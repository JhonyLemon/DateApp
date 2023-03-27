package pl.jhonylemon.dateapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import pl.jhonylemon.dateapp.MainActivity;

public class MainActivityViewModel extends ViewModel {


    private final MutableLiveData<Integer> fragment = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actionBarVisible = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressBarVisible = new MutableLiveData<>();
    private final MutableLiveData<Boolean> bottomBarVisible = new MutableLiveData<>();
    private final MutableLiveData<String> profilePicture = new MutableLiveData<>();
    private final MutableLiveData<String> uuid = new MutableLiveData<>();
    private final MutableLiveData<Boolean> enableLocation = new MutableLiveData<>();
    private final MutableLiveData<MainActivity> mainActivity = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isGpsChangeReceiverRegistered = new MutableLiveData<>();

    public LiveData<Boolean> isGpsChangeReceiverRegistered() {
        return isGpsChangeReceiverRegistered;
    }

    public void isGpsChangeReceiverRegistered(Boolean isEnabled) {
        isGpsChangeReceiverRegistered.setValue(isEnabled);
    }

    public LiveData<Integer> getFragment() {
        return fragment;
    }

    public LiveData<Boolean> getActionBarVisible() {
        return actionBarVisible;
    }

    public LiveData<Boolean> getProgressBarVisible() {
        return progressBarVisible;
    }

    public LiveData<Boolean> getBottomBarVisible() {
        return bottomBarVisible;
    }

    public LiveData<String> getProfilePicture() {
        return profilePicture;
    }

    public LiveData<Boolean> getEnableLocation() {
        return enableLocation;
    }

    public MainActivity getMainActivity() {
        return mainActivity.getValue();
    }

    public void setFragment(Integer progress){
        fragment.setValue(progress);
    }

    public void setActionBarVisible(Boolean visible){
        actionBarVisible.setValue(visible);
    }

    public void setProgressBarVisible(Boolean visible){
        progressBarVisible.setValue(visible);
    }

    public void setBottomBarVisible(Boolean visible) {
        bottomBarVisible.setValue(visible);
    }

    public void setProfilePicture(String url) {
        profilePicture.setValue(url);
    }

    public void setEnableLocation(Boolean enableLocation) {
        this.enableLocation.setValue(enableLocation);
    }

    public void setMainActivity(MainActivity activity) {
         mainActivity.setValue(activity);
    }

    public LiveData<String> getUuid() {
        return uuid;
    }

    public void setUuid(String uuid){
        this.uuid.setValue(uuid);
    }

    public void init() {
        isGpsChangeReceiverRegistered.setValue(false);
        enableLocation.setValue(false);
    }
}
