package pl.jhonylemon.dateapp.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import pl.jhonylemon.dateapp.entity.UserDetails;
import pl.jhonylemon.dateapp.entity.UserPreferences;

public class HelperFunctions {

    private HelperFunctions(){}

    public static Integer getAgeFromBirthdayDate(@NonNull LocalDate birthdayDate){
        return Period.between(
                birthdayDate,
                LocalDate.now()
        ).getYears();
    }

    public static boolean ageBetween(@NonNull Integer age, @NonNull Integer minAge,@Nullable Integer maxAge){
        return age >= minAge && (maxAge == null || age <= maxAge);
    }

    public static Predicate<UserDetails> ageBetween(UserPreferences userPreferences){
        return detail-> HelperFunctions.ageBetween(HelperFunctions.getAgeFromBirthdayDate(stringToLocalDate(detail.getBirthdayDate())),userPreferences.getMinAge(),userPreferences.getMaxAge());
    }

    public static Predicate<UserDetails> genderMatch(UserPreferences userPreferences){
        return detail-> {
            List<Integer> genders = userPreferences.getGenderId()!=null ? userPreferences.getGenderId() : new ArrayList<>();
            if(genders.isEmpty()){
                return true;
            }else{
                return genders.contains(detail.getGenderId());
            }
        };
    }

    public static Predicate<UserDetails> orientationMatch(UserPreferences userPreferences){
        return detail-> {
            List<Integer> genders = userPreferences.getOrientationId()!=null ? userPreferences.getOrientationId() : new ArrayList<>();
            if(genders.isEmpty()){
                return true;
            }else{
                return genders.contains(detail.getOrientationId());
            }
        };
    }

    public static Predicate<UserDetails> passionsMatch(UserPreferences userPreferences){
        return detail-> {
            List<Integer> genders = userPreferences.getPassions()!=null ? userPreferences.getPassions() : new ArrayList<>();
            if(genders.isEmpty()){
                return true;
            }else{
                return genders.stream().anyMatch(detail.getPassions()::contains);
            }
        };
    }

    public static DateTimeFormatter dateTimeFormatter(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

    public static LocalDate stringToLocalDate(String date){
        try {
            return LocalDate.parse(date, dateTimeFormatter());
        }catch (DateTimeParseException e){
            return LocalDate.now();
        }
    }

    public static String localDateToString(LocalDate date){
        return date.format(dateTimeFormatter());
    }


}
