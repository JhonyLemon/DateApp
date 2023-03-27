package pl.jhonylemon.dateapp.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChat implements Parcelable,Serializable {


    private Map<String,UserChatDetail> chats;

    public UserChat() {
        this.chats = new HashMap<>();
    }

    protected UserChat(Parcel in) {
        int sizeOfMap = in.readInt();
        for(int i=0; i<sizeOfMap; i++){
            chats.put(in.readString(),in.readParcelable(UserChatDetail.class.getClassLoader()));
        }
    }

    public static final Creator<UserChat> CREATOR = new Creator<>() {
        @Override
        public UserChat createFromParcel(Parcel in) {
            return new UserChat(in);
        }

        @Override
        public UserChat[] newArray(int size) {
            return new UserChat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chats.size());
        chats.forEach((k,v)->{
            dest.writeString(k);
            dest.writeParcelable(v,flags);
        });
    }
}
