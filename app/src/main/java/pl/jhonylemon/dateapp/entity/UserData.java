package pl.jhonylemon.dateapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserData implements Parcelable,Serializable {

    private String userUID;
    private UserDetails userDetails;
    private UserPairs userPairs;
    private UserChat userChat;
    private UserPreferences userPreferences;
    public UserData() {
        this.userDetails = new UserDetails();
        this.userPairs = new UserPairs();
        this.userPreferences=new UserPreferences();
        this.userChat = new UserChat();
    }

    protected UserData(Parcel in) {
        userUID = in.readString();
        userDetails = in.readParcelable(UserDetails.class.getClassLoader());
        userPairs = in.readParcelable(UserPairs.class.getClassLoader());
        userPreferences = in.readParcelable(UserPreferences.class.getClassLoader());
        userChat = in.readParcelable(UserChat.class.getClassLoader());
    }

    public static final Creator<UserData> CREATOR = new Creator<>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userUID);
        dest.writeParcelable(userDetails,flags);
        dest.writeParcelable(userPairs,flags);
        dest.writeParcelable(userPreferences,flags);
        dest.writeParcelable(userChat,flags);
    }
}
