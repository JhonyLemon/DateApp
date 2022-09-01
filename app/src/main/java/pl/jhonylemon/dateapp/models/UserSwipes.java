package pl.jhonylemon.dateapp.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSwipes implements Serializable {

    private Map<String,Boolean> swipes;

    public UserSwipes(Map<String, Boolean> swipes) {
        this.swipes = swipes;
    }

    public UserSwipes() {
        this.swipes=new HashMap<>();
    }
}
