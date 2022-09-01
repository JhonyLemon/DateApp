package pl.jhonylemon.dateapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPairs implements Serializable {

    List<String> pairs;

    public UserPairs(List<String> pairs) {
        this.pairs = pairs;
    }

    public UserPairs() {
        this.pairs = new ArrayList<>();
    }
}
