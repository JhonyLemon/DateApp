package pl.jhonylemon.dateapp.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserData implements Serializable {

    private String userUID;
    private UserDetails userDetails;
    private UserPhotos userPhotos;

    public UserData() {
        this.userDetails = new UserDetails();
        this.userPhotos = new UserPhotos();
    }
}
