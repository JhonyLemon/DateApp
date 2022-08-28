package com.example.dateapp.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements Serializable {

    private UserData userData;
    private UserPairs userPairs;
    private UserSwipes userSwipes;
    private Boolean isNewUser;

    public User(UserData userData, UserPairs userPairs, UserSwipes userSwipes, Boolean isNewUser) {
        this.userData = userData;
        this.userPairs = userPairs;
        this.userSwipes = userSwipes;
        this.isNewUser = isNewUser;
    }

    public User() {
        this.userData = new UserData();
        this.userPairs = new UserPairs();
        this.userSwipes = new UserSwipes();
        this.isNewUser = true;
    }
}
