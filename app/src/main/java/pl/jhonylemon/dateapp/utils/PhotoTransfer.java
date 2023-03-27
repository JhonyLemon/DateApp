package pl.jhonylemon.dateapp.utils;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PhotoTransfer {

    private StorageReference storageReference;

    public PhotoTransfer() {
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public Task<List<String>> getPhotos(@NonNull String uid) {
        return storageReference.child(uid).listAll()
                .continueWithTask(task->{
                    if(task.isSuccessful()){
                        List<StorageReference> storageReferences = task.getResult().getItems();
                        List<Task<Uri>> uris = new ArrayList<>();
                        for (var ref : storageReferences) {
                            uris.add(ref.getDownloadUrl());
                        }
                        return Tasks.whenAllComplete(uris);
                    }
                    return Tasks.forCanceled();
                })
                .continueWith(task -> {
                    if(task.isSuccessful()){
                        return task.getResult().stream()
                                .filter(Task::isSuccessful)
                                .map(Task::getResult)
                                .map(u->(Uri)u)
                                .map(Uri::getPath)
                                .collect(Collectors.toList());
                    }
                    return new ArrayList<>();
                });
    }

    public StorageReference getPhoto(@NonNull String url) {
        return  FirebaseStorage.getInstance().getReferenceFromUrl(url);
    }

    public Task<String> putPhoto(@NonNull String uid,@NonNull String url, @NonNull Bitmap image, @NonNull String ext) {
        return deletePhoto(url)
                .continueWithTask(task -> {
                    if(task.isSuccessful()){
                        return postPhoto(uid,image,ext);
                    }
                    return null;
                });
    }

    public Task<String> postPhoto(@NonNull String uid,@NonNull Bitmap image, @NonNull String ext) {

        String fileName = UUID.randomUUID().toString()+"."+ext;
        StorageReference filepath = storageReference.child(uid).child(fileName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return filepath
                    .putBytes(baos.toByteArray())
                    .continueWithTask(task -> {
                        if(task.isSuccessful()){
                            return filepath.getDownloadUrl();
                        }
                        return null;
                    })
                    .continueWith(task -> {
                        if(task.isSuccessful()){
                            return task.getResult().toString();
                        }
                        return null;
                    });
    }

    public Task<Void> deletePhoto(@NonNull String url) {
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        return reference.delete();
    }
}
