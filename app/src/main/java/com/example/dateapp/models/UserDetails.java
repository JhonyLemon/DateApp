package com.example.dateapp.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetails implements Serializable {

    private String name;

    private LocalDate birthdayDate;

    private Integer genderId;
    private Integer orientationId;

    private List<Integer> passions;

    private String description;

    private UserPreferences userPreferences;

    public UserDetails(String name, LocalDate birthdayDate, Integer genderId, Integer orientationId, List<Integer> passions, String description, UserPreferences userPreferences) {
        this.name = name;
        this.birthdayDate = birthdayDate;
        this.genderId = genderId;
        this.orientationId = orientationId;
        this.passions = passions;
        this.description = description;
        this.userPreferences = userPreferences;
    }

    public UserDetails() {
        this.passions= new ArrayList<>();
        this.description="";
        this.userPreferences=new UserPreferences();
    }
}
