package pl.jhonylemon.dateapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPreferences implements Parcelable,Serializable {

    private Integer minAge;
    private Integer maxAge;
    private List<Integer> genderId;
    private List<Integer> orientationId;
    private List<Integer> passions;
    private Integer maxDistance;

    public UserPreferences(Integer minAge, Integer maxAge, List<Integer> genderId, List<Integer> orientationId, List<Integer> passions, Integer maxDistance) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.genderId = genderId;
        this.orientationId = orientationId;
        this.passions = passions;
        this.maxDistance = maxDistance;
    }

    public UserPreferences() {
        this.genderId =new ArrayList<>();
        this.passions = new ArrayList<>();
        this.orientationId = new ArrayList<>();
        this.minAge=18;
        this.maxAge=100;
        this.maxDistance=80;
    }

    protected UserPreferences(Parcel in) {

    }

    public static final Creator<UserPreferences> CREATOR = new Creator<UserPreferences>() {
        @Override
        public UserPreferences createFromParcel(Parcel in) {
            return new UserPreferences(in);
        }

        @Override
        public UserPreferences[] newArray(int size) {
            return new UserPreferences[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(minAge);
        dest.writeInt(maxAge);
        dest.writeList(genderId);
        dest.writeList(orientationId);
        dest.writeList(passions);
        dest.writeInt(maxDistance);
    }
}
