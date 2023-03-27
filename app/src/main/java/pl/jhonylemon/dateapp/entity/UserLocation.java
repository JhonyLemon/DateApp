package pl.jhonylemon.dateapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLocation implements Parcelable,Serializable {

    private String userUID;
    private Double longitude;
    private Double latitude;
    private String geoHash;

    public UserLocation() {
    }

    public UserLocation(String userUID, Double longitude, Double latitude, String geoHash) {
        this.userUID = userUID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.geoHash = geoHash;
    }

    protected UserLocation(Parcel in) {
        this.userUID = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.geoHash = in.readString();
    }

    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userUID);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(geoHash);
    }
}
