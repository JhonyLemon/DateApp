package com.example.dateapp.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserData implements Serializable {

    private UserDetails userDetails;
    private UserPhotos userPhotos;

    public UserData(UserDetails userDetails, UserPhotos userPhotos) {
        this.userDetails = userDetails;
        this.userPhotos = userPhotos;
    }

    public UserData() {
        this.userDetails = new UserDetails();
        this.userPhotos = new UserPhotos();
    }
}
