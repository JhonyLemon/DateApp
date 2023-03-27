package pl.jhonylemon.dateapp.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPairs implements Parcelable,Serializable {

    Map<String,UserPair> pairs;

    public UserPairs(Map<String,UserPair> pairs) {
        this.pairs = pairs;
    }

    public UserPairs() {
        this.pairs = new HashMap<>();
    }

    protected UserPairs(Parcel in) {
        Bundle bundle = in.readBundle(Bundle.class.getClassLoader());
        for (String key : bundle.keySet()) {
            pairs.put(key, bundle.getParcelable(key));
        }
    }

    public static final Creator<UserPairs> CREATOR = new Creator<UserPairs>() {
        @Override
        public UserPairs createFromParcel(Parcel in) {
            return new UserPairs(in);
        }

        @Override
        public UserPairs[] newArray(int size) {
            return new UserPairs[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        pairs.forEach(bundle::putParcelable);
        dest.writeBundle(bundle);
    }
}
