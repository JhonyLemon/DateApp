package pl.jhonylemon.dateapp.viewmodels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pl.jhonylemon.dateapp.models.UserData;
import pl.jhonylemon.dateapp.models.UserPreferences;
import com.google.firebase.auth.FirebaseUser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuthenticationViewModel extends ViewModel {

    private final MutableLiveData<UserData> userData = new MutableLiveData<UserData>();

    private final MutableLiveData<Boolean> startUserCreation = new MutableLiveData<Boolean>();

    private final MutableLiveData<Integer> fragment = new MutableLiveData<>();

    private final MutableLiveData<Boolean> actionBarVisible = new MutableLiveData<>();

    private final MutableLiveData<Boolean> progressBarVisible = new MutableLiveData<>();

    public LiveData<Boolean> getStartUserCreation() {
        return startUserCreation;
    }

    public LiveData<UserData> getUserData() {
        return userData;
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

    public void setFragment(Integer progress){
        fragment.setValue(progress);
    }

    public void setActionBarVisible(Boolean visible){
        actionBarVisible.setValue(visible);
    }

    public void setProgressBarVisible(Boolean visible){
        progressBarVisible.setValue(visible);
    }


    public void init(){
        userData.setValue(new UserData());
    }



    public void setName(String name){
        userData.getValue().getUserDetails().setName(name);
    }

    public String getName(){
       return userData.getValue().getUserDetails().getName();
    }


    public void setBirthdayDate(LocalDate date){
        userData.getValue().getUserDetails().setBirthdayDate(java.util.Date.from(date.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()));
    }

    public LocalDate getBirthdayDate(){
       return this.userData.getValue().getUserDetails().getBirthdayDate()==null ? null : Instant.ofEpochMilli(userData.getValue().getUserDetails().getBirthdayDate().getTime())
               .atZone(ZoneId.systemDefault())
               .toLocalDate();
    }

    public void setGender(Integer index){
        userData.getValue().getUserDetails().setGenderId(index);
    }

    public Integer getGender(){
       return userData.getValue().getUserDetails().getGenderId();
    }

    public void setOrientation(Integer id){
        userData.getValue().getUserDetails().setOrientationId(id);
    }

    public Integer getOrientation(){
       return userData.getValue().getUserDetails().getOrientationId();
    }

    public void setPassions(List<Integer> passions){
        userData.getValue().getUserDetails().setPassions(passions);
    }

    public List<Integer> getPassions(){
       return userData.getValue().getUserDetails().getPassions();
    }

    public void setDescription(String description){
        userData.getValue().getUserDetails().setDescription(description);
    }

    public String getDescription(){
       return userData.getValue().getUserDetails().getDescription();
    }

    public void setPhotos(List<Bitmap> photos){
        userData.getValue().getUserPhotos().setPhotos(photos);
        for(Integer i=0; i<photos.size(); i++)
        {
            userData.getValue().getUserPhotos().getPhotosUrl().add(userData.getValue().getUserUID()+"/"+i.toString());
        }
    }

    public List<Bitmap> getPhotos(){
       return userData.getValue().getUserPhotos().getPhotos();
    }

    public void setPreferences(UserPreferences preferences){
        userData.getValue().getUserDetails().setUserPreferences(preferences);
    }

    public UserPreferences getPreferences(){
       return userData.getValue().getUserDetails().getUserPreferences();
    }

    public void setUserUID(String userUID){
        userData.getValue().setUserUID(userUID);
    }

    public String getUserUID(){
        return userData.getValue().getUserUID();
    }


}
