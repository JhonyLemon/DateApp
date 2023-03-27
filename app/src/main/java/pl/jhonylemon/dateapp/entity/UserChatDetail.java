package pl.jhonylemon.dateapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChatDetail implements Parcelable,Serializable {


    private String lastMessage;
    private Map<String,UserChatMessage> messages;

    public UserChatDetail() {
        this.messages = new HashMap<>();
    }

    protected UserChatDetail(Parcel in) {
        lastMessage = in.readString();
        int sizeOfMap = in.readInt();
        for(int i=0; i<sizeOfMap; i++){
            messages.put(in.readString(),in.readParcelable(UserChatMessage.class.getClassLoader()));
        }
    }

    public static final Creator<UserChatDetail> CREATOR = new Creator<>() {
        @Override
        public UserChatDetail createFromParcel(Parcel in) {
            return new UserChatDetail(in);
        }

        @Override
        public UserChatDetail[] newArray(int size) {
            return new UserChatDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lastMessage);
        dest.writeInt(messages.size());
        messages.forEach((k,v)->{
            dest.writeString(k);
            dest.writeParcelable(v,flags);
        });
    }
}
