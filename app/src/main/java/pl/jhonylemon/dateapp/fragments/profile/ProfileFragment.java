package pl.jhonylemon.dateapp.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.Tasks;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentProfileBinding;
import pl.jhonylemon.dateapp.fragments.BaseFragment;
import pl.jhonylemon.dateapp.utils.DataTransfer;
import pl.jhonylemon.dateapp.utils.HelperFunctions;

public class ProfileFragment extends BaseFragment {

    public static final String TAG="ProfileFragment";
    private FragmentProfileBinding binding;

    private DataTransfer dataTransfer = new DataTransfer();

    public ProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController= Navigation.findNavController(view);
        this.mainActivityViewModel.setBottomBarVisible(false);
        this.mainActivityViewModel.setActionBarVisible(true);

        Glide.with(getContext())
                .load(mainActivityViewModel.getProfilePicture().getValue())
                .signature(new ObjectKey(mainActivityViewModel.getProfilePicture().getValue()))
                .into(binding.profilePicture);

        dataTransfer.getName(dataTransfer.getUUID()).get().continueWithTask(task->{
            if(task.isSuccessful()){
                binding.nameAge.setText(task.getResult().getValue(String.class));
                return dataTransfer.getBirthdayDate(dataTransfer.getUUID()).get();
            }else{
                return Tasks.forCanceled();
            }
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(binding!=null){
                String age = HelperFunctions.getAgeFromBirthdayDate(HelperFunctions.stringToLocalDate(task.getResult().getValue(String.class))).toString();
                binding.nameAge.setText(binding.nameAge.getText().toString()+" "+age);
                }
            }
        });
        binding.editProfile.setOnClickListener(v->this.navController.navigate(R.id.profileEditFragment));
        binding.settings.setOnClickListener(v->this.navController.navigate(R.id.settingsFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}
