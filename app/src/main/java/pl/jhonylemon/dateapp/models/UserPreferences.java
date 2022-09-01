package pl.jhonylemon.dateapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPreferences implements Serializable {

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
}
