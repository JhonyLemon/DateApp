package pl.jhonylemon.dateapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPair implements Parcelable,Serializable {

    private String myUUID;
    private Boolean myVote;
    private String theirUUID;
    private Boolean theirVote;

    public UserPair() {
    }

    public UserPair(String myUUID, Boolean myVote, String theirUUID, Boolean theirVote) {
        this.myUUID = myUUID;
        this.myVote = myVote;
        this.theirUUID = theirUUID;
        this.theirVote = theirVote;
    }

    protected UserPair(Parcel in) {
        this.myUUID = in.readString();
        this.myVote = in.readBoolean();
        this.theirUUID = in.readString();
        this.theirVote = in.readBoolean();
    }

    public static final Creator<UserPair> CREATOR = new Creator<UserPair>() {
        @Override
        public UserPair createFromParcel(Parcel in) {
            return new UserPair(in);
        }

        @Override
        public UserPair[] newArray(int size) {
            return new UserPair[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myUUID);
        dest.writeBoolean(myVote);
        dest.writeString(theirUUID);
        dest.writeBoolean(theirVote);
    }
}
