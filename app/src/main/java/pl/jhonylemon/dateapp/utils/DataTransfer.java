package pl.jhonylemon.dateapp.utils;


import static pl.jhonylemon.dateapp.utils.HelperFunctions.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import pl.jhonylemon.dateapp.entity.UserChatMessage;
import pl.jhonylemon.dateapp.entity.UserData;
import pl.jhonylemon.dateapp.entity.UserDetails;
import pl.jhonylemon.dateapp.entity.UserLocation;
import pl.jhonylemon.dateapp.entity.UserPair;
import pl.jhonylemon.dateapp.entity.UserPairs;
import pl.jhonylemon.dateapp.entity.UserPreferences;

public class DataTransfer {

    private FirebaseFirestore firestore;
    private DatabaseReference realtime;


    public DataTransfer() {
        firestore = FirebaseFirestore.getInstance();
        realtime = FirebaseDatabase.getInstance().getReference();
    }

    //
    //  Realtime database
    //

    public Task<Void> setUUID(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("userUID")
                .setValue(uuid);
    }

    public DatabaseReference getUUID(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("userUID");
    }

    public @Nullable
    String getUUID() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    public Task<String> getUuidTask() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Tasks.forCanceled();
        }
        return mAuth
                .getCurrentUser()
                .reload()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return mAuth.getCurrentUser();
                    }
                    return Tasks.forCanceled();
                })
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return ((FirebaseUser) task.getResult()).getUid();
                    }
                    return null;
                });
    }

    public Task<Void> setNewUser(@NonNull String uuid, @NonNull Boolean isNewUser) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("isNewUser")
                .setValue(isNewUser);
    }

    public DatabaseReference getNewUser(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("isNewUser");
    }

    public Task<Void> setName(@NonNull String uuid, @NonNull String name) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("name")
                .setValue(name);
    }

    public DatabaseReference getName(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("name");
    }

    public Task<Void> setBirthdayDate(@NonNull String uuid, @NonNull String date) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("birthdayDate")
                .setValue(date);
    }

    public DatabaseReference getBirthdayDate(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("birthdayDate");
    }

    public Task<Void> setDescription(@NonNull String uuid, @NonNull String description) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("description")
                .setValue(description);
    }

    public DatabaseReference getDescription(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("description");
    }

    public Task<Void> setGenderId(@NonNull String uuid, @NonNull Integer genderId) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("genderId")
                .setValue(genderId);
    }

    public DatabaseReference getGenderId(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("genderId");
    }

    public Task<Void> setOrientationId(@NonNull String uuid, @NonNull Integer orientationId) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("orientationId")
                .setValue(orientationId);
    }

    public DatabaseReference getOrientationId(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("orientationId");
    }

    public Task<Void> setPassions(@NonNull String uuid, @NonNull List<Integer> passions) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("passions")
                .setValue(passions);
    }

    public DatabaseReference getPassions(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("passions");
    }

    public Task<Void> setPrefferedAge(@NonNull String uuid, @NonNull List<Integer> ageRange) {
        return realtime.child("users")
                .child(uuid)
                .child("userPreferences")
                .child("minAge")
                .setValue(ageRange.get(0))
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return realtime.child("users")
                                .child(uuid)
                                .child("userPreferences")
                                .child("maxAge")
                                .setValue(ageRange.get(1));
                    }
                    return task;
                });
    }

    public Task<Void> setPrefferedDistance(@NonNull String uuid, @NonNull Integer distance) {
        return realtime.child("users")
                .child(uuid)
                .child("userPreferences")
                .child("maxDistance")
                .setValue(distance);
    }

    public Task<Void> setPrefferedOrientation(@NonNull String uuid, @NonNull List<Integer> orientations) {
        return realtime.child("users")
                .child(uuid)
                .child("userPreferences")
                .child("orientationId")
                .setValue(orientations);
    }

    public Task<Void> setPrefferedGender(@NonNull String uuid, @NonNull List<Integer> genders) {
        return realtime.child("users")
                .child(uuid)
                .child("userPreferences")
                .child("genderId")
                .setValue(genders);
    }

    public Task<Void> setPrefferedPassions(@NonNull String uuid, @NonNull List<Integer> passions) {
        return realtime.child("users")
                .child(uuid)
                .child("userPreferences")
                .child("passions")
                .setValue(passions);
    }

    public Task<Void> setPreferences(@NonNull String uuid, @NonNull UserPreferences userPreferences) {
        return realtime.child("users")
                .child(uuid)
                .child("userPreferences")
                .setValue(userPreferences);
    }

    public DatabaseReference getPreferences(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userPreferences");
    }

    public Task<Void> setUserPhotos(@NonNull String uuid, @NonNull List<String> photos) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("userPhotos")
                .child("photos")
                .setValue(photos);
    }


    public DatabaseReference getUserPhotos(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails")
                .child("userPhotos")
                .child("photos");
    }

    public Task<Void> setUserPair(@NonNull String uuid, @NonNull UserPair pair) {
        return realtime.child("users")
                .child(uuid)
                .child("userPairs")
                .child("pairs")
                .child(pair.getTheirUUID())
                .setValue(pair);
    }

    public DatabaseReference getUserPairs(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userPairs");
    }

    public DatabaseReference getUserPair(@NonNull String uuid, @NonNull String theirUuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userPairs")
                .child("pairs")
                .child(theirUuid);
    }

    public Task<Void> setPairVote(@NonNull String myUuid, @NonNull String theirUuid, Boolean vote) {
        AtomicReference<UserPair> myPair = new AtomicReference<>();
        AtomicReference<UserPair> theirPair = new AtomicReference<>();
        return getUserPair(myUuid, theirUuid).get().continueWithTask(task -> {
            if (task.isSuccessful()) {
                myPair.set(Optional.ofNullable(task.getResult().getValue(UserPair.class)).orElse(new UserPair(myUuid, vote, theirUuid, null)));
                myPair.get().setMyVote(vote);
                return getUserPair(theirUuid, myUuid).get();
            }
            return Tasks.forCanceled();
        }).continueWithTask(task -> {
            if (task.isSuccessful()) {
                theirPair.set(Optional.ofNullable(task.getResult().getValue(UserPair.class)).orElse(new UserPair(theirUuid, null, myUuid, vote)));
                theirPair.get().setTheirVote(vote);
                return Tasks.whenAllComplete(List.of(
                        setUserPair(myUuid, myPair.get()),
                        setUserPair(theirUuid, theirPair.get())
                ));
            }
            return Tasks.forCanceled();
        }).continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult().stream().allMatch(Task::isSuccessful)) {
                return Tasks.forResult(null);
            } else {
                return setPairVote(myUuid, theirUuid, vote);
            }
        });
    }

    public DatabaseReference getUserDetails(@NonNull String uuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userDetails");
    }

    public Task<List<String>> filterByPairs(@NonNull String uuid, @NonNull List<String> uuids) {
        return getUserPairs(uuid).get().continueWith(task -> {
            if (task.isSuccessful()) {
                UserPairs pairs = Optional.ofNullable(task.getResult().getValue(UserPairs.class)).orElse(new UserPairs());
                Map<String,UserPair> pair = Optional.ofNullable(pairs.getPairs()).orElse(new HashMap<>());
                pair.forEach((k, v) -> {
                    if (v.getMyVote() != null) {
                        uuids.remove(v.getTheirUUID());
                    }
                });
            }
            return uuids;
        });
    }

    public Task<List<String>> filterByPreferences(@NonNull String uuid, @NonNull List<String> uuids) {
        List<Task<?>> tasks = new ArrayList<>();
        AtomicReference<UserPreferences> userPreferences = new AtomicReference<>();
        for (String uuidTemp : uuids) {
            tasks.add(getUserDetails(uuidTemp).get().continueWith(task -> {
                if (task.isSuccessful()) {
                    return task.getResult().getValue(UserDetails.class);
                }
                return Tasks.forCanceled();
            }));
        }

        return getPreferences(uuid).get().continueWithTask(task -> {
            if (task.isSuccessful()) {
                userPreferences.set(task.getResult().getValue(UserPreferences.class));
                return Tasks.whenAllComplete(tasks);
            }
            return Tasks.forCanceled();
        }).continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().stream()
                        .filter(Task::isSuccessful)
                        .map(Task::getResult)
                        .map(t -> (UserDetails) t)
                        .collect(Collectors.toList());
            }
            return new ArrayList<UserDetails>();
        }).continueWith(task -> {
            if (task.isSuccessful()) {
                List<UserDetails> details = task.getResult();
                return details.stream()
                        //is new user
                        .filter(u -> !u.getIsNewUser())
                        //age preference
                        .filter(ageBetween(userPreferences.get()))
                        //gender preference
                        .filter(genderMatch(userPreferences.get()))
                        //orientation preference
                        .filter(orientationMatch(userPreferences.get()))
                        //passions preference
                        .filter(passionsMatch(userPreferences.get()))
                        .map(UserDetails::getUserUID)
                        .collect(Collectors.toList());
            }
            return uuids;
        });
    }

    public DatabaseReference getUserChat(@NonNull String uuid,@NonNull String theirUuid) {
        return realtime.child("users")
                .child(uuid)
                .child("userChat")
                .child(theirUuid);
    }

    public DatabaseReference getLastMessage(@NonNull String uuid,@NonNull String theirUuid){
        return realtime.child("users")
                .child(uuid)
                .child("userChat")
                .child(theirUuid)
                .child("lastMessage");
    }

    public Task<Void> addUserChatMessage(@NonNull String uuid,@NonNull String theirUuid, @NonNull UserChatMessage userChatMessage) {
        return realtime.child("users")
                .child(uuid)
                .child("userChat")
                .child(theirUuid)
                .child("messages")
                .push().setValue(userChatMessage).continueWithTask(task -> {
                    if(task.isSuccessful()){
                        return realtime.child("users")
                                .child(theirUuid)
                                .child("userChat")
                                .child(uuid)
                                .child("messages")
                                .push().setValue(userChatMessage);
                    }
                    return Tasks.forCanceled();
                })
                .continueWithTask(task -> {
                    if(task.isSuccessful()){
                        return realtime.child("users")
                                .child(uuid)
                                .child("userChat")
                                .child(theirUuid)
                                .child("lastMessage")
                                .setValue(userChatMessage.getMessage());
                    }
                    return Tasks.forCanceled();
                })
                .continueWithTask(task -> {
                    if(task.isSuccessful()){
                        return realtime.child("users")
                                .child(theirUuid)
                                .child("userChat")
                                .child(uuid)
                                .child("lastMessage")
                                .setValue(userChatMessage.getMessage());
                    }
                    return Tasks.forCanceled();
                });
    }

    //
    //Firestore database
    //

    public Task<Void> setDocument(@NonNull String uuid) {
        return firestore.collection("users").document(uuid)
                .set(new UserLocation(uuid, null, null, null));
    }

    public Task<Void> setLocation(@NonNull UserLocation userLocation) {
        return firestore.collection("users").document(userLocation.getUserUID())
                .update(new HashMap<>(Map.of(
                        "geoHash", userLocation.getGeoHash(),
                        "latitude", userLocation.getLatitude(),
                        "longitude", userLocation.getLongitude()
                )));
    }

    public Task<UserLocation> getLocation(@NonNull String uuid) {
        return firestore.collection("users").document(uuid)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return task.getResult().toObject(UserLocation.class);
                    }
                    return null;
                });
    }

    //
    //Mixed
    //

    public Task<Void> deleteUser(@NonNull String uuid){
        return realtime.child("users")
                .child(uuid).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        UserData userData = task.getResult().getValue(UserData.class);
                        List<Task<?>> tasks = new ArrayList<>();
                        userData.getUserPairs().getPairs().keySet().forEach(u->{
                            tasks.add(realtime.child("users").child(u).child("userPairs").child("pairs").child(uuid).removeValue());
                        });
                        userData.getUserChat().getChats().keySet().forEach(u->{
                            tasks.add(realtime.child("users").child(u).child("userChat").child(uuid).removeValue());
                        });
                        return Tasks.whenAllComplete(tasks);
                    }
                    return Tasks.forCanceled();
                })
                .continueWithTask(task -> {
                    if(task.isSuccessful()){
                        return realtime.child("users").child(uuid).removeValue();
                    }
                    return Tasks.forCanceled();
                })
                .continueWithTask(task -> {
                    if(task.isSuccessful()){
                        return firestore.collection("users").document(uuid).delete();
                    }
                    return Tasks.forCanceled();
                });
    }

    public Task<List<String>> getUsersUIDSInRange(@NonNull String uuid) {

        AtomicReference<UserPreferences> preferences = new AtomicReference<>();
        AtomicReference<UserLocation> location = new AtomicReference<>();

        return getPreferences(uuid).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        preferences.set(task.getResult().getValue(UserPreferences.class));
                        return getLocation(uuid);
                    }
                    return Tasks.forCanceled();
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        location.set(task.getResult());
                        return Tasks.forResult(null);
                    }
                    return Tasks.forCanceled();
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        final double distanceInM = 1000 * preferences.get().getMaxDistance();
                        final GeoLocation center = new GeoLocation(location.get().getLatitude(), location.get().getLongitude());
                        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, distanceInM);
                        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                        for (GeoQueryBounds b : bounds) {
                            Query q = firestore.collection("users")
                                    .orderBy("geoHash")
                                    .startAt(b.startHash)
                                    .endAt(b.endHash);
                            tasks.add(q.get());
                        }

                        return Tasks.whenAllComplete(tasks);
                    }
                    return Tasks.forCanceled();
                })
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<UserLocation> userLocations = task.getResult().stream()
                                .filter(Task::isSuccessful)
                                .map(Task::getResult)
                                .map(l -> (QuerySnapshot) l)
                                .map(QuerySnapshot::getDocuments)
                                .flatMap(List::stream)
                                .map(d -> d.toObject(UserLocation.class))
                                .filter(l -> !l.getUserUID().equals(uuid))
                                .collect(Collectors.toList());
                        return userLocations;
                    }
                    return new ArrayList<UserLocation>();
                })
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        GeoLocation docLocation = new GeoLocation(
                                location.get().getLatitude(),
                                location.get().getLongitude()
                        );
                        final double maxDistanceInM = 1000 * preferences.get().getMaxDistance();
                        List<String> uuids = task.getResult().stream()
                                .filter(userLocation -> {
                                    double distanceInM = GeoFireUtils.getDistanceBetween(
                                            docLocation,
                                            new GeoLocation(userLocation.getLatitude(), userLocation.getLongitude())
                                    );
                                    if (distanceInM <= maxDistanceInM) {
                                        return true;
                                    }
                                    return false;
                                })
                                .map(UserLocation::getUserUID)
                                .collect(Collectors.toList());
                        return uuids;
                    }
                    return new ArrayList<String>();
                }).continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return filterByPreferences(uuid, task.getResult());
                    }
                    return Tasks.forCanceled();
                }).continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return filterByPairs(uuid, (List<String>) task.getResult());
                    }
                    return Tasks.forCanceled();
                });
    }
}
//TODO move to realtime db