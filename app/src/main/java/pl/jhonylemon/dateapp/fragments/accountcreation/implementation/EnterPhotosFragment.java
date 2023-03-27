package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import pl.jhonylemon.dateapp.databinding.GridViewPhotoBinding;
import pl.jhonylemon.dateapp.databinding.GridviewPhotoItemBinding;
import pl.jhonylemon.dateapp.fragments.CropImageFragment;
import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentEnterPhotosBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreationFragment;
import pl.jhonylemon.dateapp.utils.PhotoTransfer;

public class EnterPhotosFragment extends AccountCreationFragment {

    public static final String TAG = "EnterPhotosFragment";
    private static final Integer ProgressBarProgress = 8;
    private FragmentEnterPhotosBinding binding;

    public static final int PICK_IMAGE = 1;

    private Bitmap photo;
    private String ext;

    private ActivityResultLauncher<String> mGetContent;
    private final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

    private List<GridviewPhotoItemBinding> photoItems;

    private PhotoTransfer photoTransfer = new PhotoTransfer();

    private List<String> uris = new ArrayList<>();

    public EnterPhotosFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterPhotosBinding.inflate(inflater, container, false);

        binding.next.setOnClickListener(this::moveNext);
        binding.previous.setOnClickListener(this::movePrevious);


        photoItems = List.of(
                binding.PhotoView.elementOne,
                binding.PhotoView.elementTwo,
                binding.PhotoView.elementThree,
                binding.PhotoView.elementFour,
                binding.PhotoView.elementFive,
                binding.PhotoView.elementSix,
                binding.PhotoView.elementSeven,
                binding.PhotoView.elementEight,
                binding.PhotoView.elementNine
        );

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null && getContext() != null) {
                ContentResolver resolver = getContext().getContentResolver();
                if (resolver != null) {

                    Bundle args = new Bundle();
                    args.putParcelable(CropImageFragment.INPUT, result);
                    getNavController().navigate(R.id.cropImageFragment, args);

                }

            }

        });

        getParentFragmentManager().setFragmentResultListener(CropImageFragment.TAG, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                Bitmap image = result.getParcelable(CropImageFragment.RESULT);
                Uri imageUri = result.getParcelable(CropImageFragment.INPUT);
                if (result != null && getContext() != null) {
                    ContentResolver resolver = getContext().getContentResolver();
                    if (resolver != null) {
                        ext = (mimeTypeMap.getExtensionFromMimeType(resolver.getType(imageUri)));
                        photo = image;
                        AtomicReference<List<String>> atomicReference = new AtomicReference<>();
                        load().continueWithTask(task -> {
                           if(task.isSuccessful()){
                               atomicReference.set(Optional.ofNullable((ArrayList<String>) task.getResult().getValue()).orElse(new ArrayList<>()));
                               return photoTransfer.postPhoto(dataTransfer.getUUID(), image, ext);
                           }
                           return Tasks.forCanceled();
                        }).continueWithTask(task -> {
                            if(task.isSuccessful()){
                                List<String> urls = atomicReference.get();
                                urls.add(task.getResult());
                                atomicReference.set(urls);
                                return dataTransfer.setUserPhotos(dataTransfer.getUUID(), atomicReference.get());
                            }
                            return Tasks.forCanceled();
                        }).addOnCompleteListener(task -> {
                            load();
                        });
                    }

                }

            }
        });

        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setNavController(Navigation.findNavController(view));
        this.getAuthenticationViewModel().setFragment(ProgressBarProgress);

        for(var photoItem : photoItems){
            photoItem.removePhotoButton.setOnClickListener(v -> {
                photoTransfer.deletePhoto((String) photoItem.imageView.getTag())
                        .continueWithTask(deleteTask -> {
                            uris.remove(photoItem.imageView.getTag());
                            return dataTransfer.setUserPhotos(dataTransfer.getUUID(),uris).addOnCompleteListener(setPhotosTask->{
                                load();
                            });
                        });
            });
            photoItem.imageView.setOnClickListener(v->{
                if(v.getTag()==null){
                    mGetContent.launch("image/*");
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    protected Task<DataSnapshot> load() {
        photoItems.forEach(item -> {
            item.removePhotoButton.setVisibility(View.GONE);
            item.imageView.setImageResource(R.drawable.ic_baseline_add_24);
            item.imageView.setTag(null);
        });
        return dataTransfer.getUserPhotos(dataTransfer.getUUID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (binding != null) {
                    uris = Optional.ofNullable((ArrayList<String>) task.getResult().getValue()).orElse(new ArrayList<>());
                    for (int i = 0; i < uris.size(); i++) {
                        Glide.with(this)
                                .load(Uri.parse(uris.get(i)))
                                .signature(new ObjectKey(uris.get(i)))
                                .into(photoItems.get(i).imageView);
                        photoItems.get(i).imageView.setTag(uris.get(i));
                        photoItems.get(i).removePhotoButton.setVisibility(View.VISIBLE);
                        validateAndEnableNext();
                    }
                }
            } else {
                load();
            }
        });
    }

    @Override
    protected Task<Void> save() {
        return null;
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterWhoToShowFragment);
    }

    @Override
    protected Boolean validate() {
        if (photoItems.stream().filter(b->b.imageView.getTag()!=null).count() >0)
            this.setValid(true);
        else
            this.setValid(false);

        this.binding.next.setEnabled(this.getValid());
        return this.getValid();
    }

    @Override
    protected Boolean validateAndEnableNext() {
        boolean valid = validate();
        if (valid) {
            binding.next.setEnabled(getValid());
        }
        return valid;
    }

}
