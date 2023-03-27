package pl.jhonylemon.dateapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetails implements Parcelable,Serializable {

    private String name;

    private String birthdayDate;

    private Integer genderId;
    private Integer orientationId;

    private List<Long> passions;

    private String description;

    private Boolean isNewUser;

    private String userUID;

    private UserPhotos userPhotos;

    public UserDetails() {
        this.passions= new ArrayList<>();
        this.description="";
        this.userPhotos = new UserPhotos();
        this.birthdayDate=null;
        this.userUID = "";
        this.isNewUser = true;
    }

    protected UserDetails(Parcel in) {
        name = in.readString();
        genderId = in.readInt();
        orientationId = in.readInt();
        birthdayDate = in.readString();
        in.readList(passions,List.class.getClassLoader());
        description = in.readString();
        userPhotos = in.readParcelable(UserPhotos.class.getClassLoader());
        userUID = in.readString();
        isNewUser = in.readBoolean();
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(genderId);
        dest.writeInt(orientationId);
        dest.writeString(birthdayDate);
        dest.writeList(passions);
        dest.writeString(description);
        dest.writeParcelable(userPhotos,flags);
        dest.writeString(userUID);
        dest.writeBoolean(isNewUser);
    }
}
