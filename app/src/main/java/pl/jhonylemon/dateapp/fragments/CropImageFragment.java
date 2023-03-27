package pl.jhonylemon.dateapp.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import pl.jhonylemon.dateapp.databinding.FragmentCropImageBinding;


public class CropImageFragment extends BaseFragment {

    private FragmentCropImageBinding binding;
    public final static String TAG ="CropImageActivity";
    public final static String RESULT= String.format(TAG+"%s","Result");
    public final static String INPUT= String.format(TAG+"%s","Input");
    public final static Integer SIZE = 380;
    public final static Integer WIDTH_ASPECT_RATIO = 3;
    public final static Integer HEIGHT_ASPECT_RATIO = 4;
    public final static Integer WIDTH = SIZE*WIDTH_ASPECT_RATIO;
    public final static Integer HEIGHT = SIZE*HEIGHT_ASPECT_RATIO;
    public final static Integer QUALITY = 25;
    private Uri imageUri;
    private Bitmap result;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCropImageBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if(bundle!=null){
            imageUri=bundle.getParcelable(INPUT);
        }

        binding.cropView.setImageUriAsync(imageUri);
        binding.cropView.setFixedAspectRatio(true);
        binding.cropView.setScaleX(1.0f);
        binding.cropView.setScaleY(1.0f);
        binding.cropView.setAspectRatio(3,4);
        binding.cropView.setAutoZoomEnabled(false);
        binding.cropView.setMaxCropResultSize(WIDTH,HEIGHT);
        binding.cropView.setMinCropResultSize(WIDTH,HEIGHT);

        binding.cropButton.setOnClickListener(v -> {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
            binding.cropView.getCroppedImage(WIDTH,HEIGHT)
                    .compress(Bitmap.CompressFormat.JPEG, QUALITY, baos);
          result = BitmapFactory.decodeByteArray(baos.toByteArray(),0,baos.size());
          Bundle args = new Bundle();
          args.putParcelable(RESULT, result);
            args.putParcelable(INPUT, imageUri);
          getParentFragmentManager().setFragmentResult(TAG, args);
          getActivity().onBackPressed();
        });

        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

}
