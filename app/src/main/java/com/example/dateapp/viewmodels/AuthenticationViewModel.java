package com.example.dateapp.viewmodels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dateapp.models.UserData;
import com.example.dateapp.models.UserPreferences;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuthenticationViewModel extends ViewModel {

    private final MutableLiveData<UserData> userData = new MutableLiveData<UserData>();

    private final MutableLiveData<Boolean> startUserCreation = new MutableLiveData<Boolean>();

    private final MutableLiveData<Integer> fragment = new MutableLiveData<>();

    public LiveData<Boolean> getStartUserCreation() {
        return startUserCreation;
    }

    public LiveData<UserData> getUserData() {
        return userData;
    }

    public LiveData<Integer> getFragment() {
        return fragment;
    }

    public void setFragment(Integer progress){
        fragment.setValue(progress);
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
        userData.getValue().getUserDetails().setBirthdayDate(date);
    }

    public LocalDate getBirthdayDate(){
        return userData.getValue().getUserDetails().getBirthdayDate();
    }

    public void setGender(Integer id){
        userData.getValue().getUserDetails().setGenderId(id);
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

}
