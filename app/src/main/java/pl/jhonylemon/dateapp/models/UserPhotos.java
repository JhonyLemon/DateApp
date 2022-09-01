package pl.jhonylemon.dateapp.models;

import android.graphics.Bitmap;


import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
public class UserPhotos implements Serializable {


    private List<Bitmap> photos;

    private List<String> photosUrl;

    public UserPhotos() {
        this.photos = new ArrayList<>();
        this.photosUrl= new ArrayList<>();
    }

    @Exclude
    public List<Bitmap> getPhotos() {
        return photos;
    }

    public List<String> getPhotosUrl() {
        return photosUrl;
    }
}
