package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.adapters.GridViewPhotosAdapter;
import pl.jhonylemon.dateapp.databinding.FragmentEnterPhotosBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreation;
import pl.jhonylemon.dateapp.viewmodels.AuthenticationViewModel;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnterPhotosFragment extends AccountCreation {

    public static final String TAG="EnterPhotosFragment";
    private static final Integer ProgressBarProgress = 8;
    private FragmentEnterPhotosBinding binding;

    public static final int PICK_IMAGE = 1;

    List<Bitmap> photos;

    GridViewPhotosAdapter gridViewPhotosAdapter;
    ActivityResultLauncher<String> mGetContent;

    public EnterPhotosFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterPhotosBinding.inflate(inflater, container, false);

        binding.next.setOnClickListener(this::moveNext);
        binding.previous.setOnClickListener(this::movePrevious);

        photos = new ArrayList<>();

        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                try {
                    if(result!=null) {
                        ParcelFileDescriptor parcelFileDescriptor =
                                getActivity().getContentResolver().openFileDescriptor(result, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        photos.add(BitmapFactory.decodeFileDescriptor(fileDescriptor));
                        if(validate())
                            save();
                        load();
                        parcelFileDescriptor.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        gridViewPhotosAdapter = new GridViewPhotosAdapter(photos, getContext(), position -> {
            if(position==photos.size()){
                mGetContent.launch("image/*");
            }else{
                photos.remove(position.intValue());
                if(validate())
                    save();
                gridViewPhotosAdapter.setPhotos(photos);
            }
        });

        binding.gridViewPhotos.setAdapter(gridViewPhotosAdapter);

        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(() -> {
            int width=binding.gridViewPhotos.getWidth()/3;
            int height= binding.gridViewPhotos.getHeight()/3;
            gridViewPhotosAdapter.setSize(width,height);
        });
        this.setNavController(Navigation.findNavController(view));
        this.getAuthenticationViewModel().setFragment(ProgressBarProgress);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    @Override
    protected void load() {
        photos=this.getAuthenticationViewModel().getPhotos();
        gridViewPhotosAdapter.setPhotos(photos);
    }

    @Override
    protected void save() {
        this.getAuthenticationViewModel().setPhotos(photos);

    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterWhoToShowFragment);
    }

    @Override
    protected Boolean validate() {
        if(photos!=null ){
            if(photos.size()>0)
                binding.next.setEnabled(true);
            else
                binding.next.setEnabled(false);
        }
        this.setValid(true);
        return this.getValid();
    }
}
