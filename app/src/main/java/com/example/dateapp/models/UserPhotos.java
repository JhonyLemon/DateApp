package com.example.dateapp.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPhotos implements Serializable {

    private List<Bitmap> photos;

    public List<Bitmap> getPhotos() {
        return photos;
    }

    public UserPhotos() {
        this.photos = new ArrayList<>();
    }
}
