package pl.jhonylemon.dateapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserPhotos implements Parcelable,Serializable {


    private List<String> photos;

    public UserPhotos() {
        photos = new ArrayList<>();
    }

    protected UserPhotos(Parcel in) {
        in.readList(photos,List.class.getClassLoader());
    }

    public static final Creator<UserPhotos> CREATOR = new Creator<UserPhotos>() {
        @Override
        public UserPhotos createFromParcel(Parcel in) {
            return new UserPhotos(in);
        }

        @Override
        public UserPhotos[] newArray(int size) {
            return new UserPhotos[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(photos);
    }
}
