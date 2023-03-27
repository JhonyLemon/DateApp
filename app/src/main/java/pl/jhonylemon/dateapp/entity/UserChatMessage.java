package pl.jhonylemon.dateapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChatMessage implements Parcelable,Serializable {

    private String message;
    private String uuid;
    private Long timestamp;

    public UserChatMessage() {
    }

    protected UserChatMessage(Parcel in) {
        uuid = in.readString();
        message = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<UserChatMessage> CREATOR = new Creator<>() {
        @Override
        public UserChatMessage createFromParcel(Parcel in) {
            return new UserChatMessage(in);
        }

        @Override
        public UserChatMessage[] newArray(int size) {
            return new UserChatMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(message);
        dest.writeLong(timestamp);
    }
}
