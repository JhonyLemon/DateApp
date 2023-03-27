package pl.jhonylemon.dateapp.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.stream.Collectors;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentSettingsBinding;
import pl.jhonylemon.dateapp.entity.UserPreferences;
import pl.jhonylemon.dateapp.fragments.BaseFragment;
import pl.jhonylemon.dateapp.entity.UserData;
import pl.jhonylemon.dateapp.utils.DataTransfer;

public class SettingsFragment extends BaseFragment {

    public static final String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;
    private NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.settingsFragment, true).build();
    private DataTransfer dataTransfer = new DataTransfer();


    public SettingsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController = Navigation.findNavController(view);
        this.mainActivityViewModel.setBottomBarVisible(false);
        this.mainActivityViewModel.setActionBarVisible(true);
        this.binding.DeleteAccount.setOnClickListener(v -> {
            String uuid = dataTransfer.getUUID();
            dataTransfer.deleteUser(uuid)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            logOut();
                        }
                    });
        });
        this.binding.LogOut.setOnClickListener(v -> {
            logOut();
        });
        binding.ageRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> age = slider.getValues();
            binding.age.setText(age.get(0).intValue() + "-" + age.get(1).intValue());
            dataTransfer.setPrefferedAge(dataTransfer.getUUID(), age.stream().map(Float::intValue).collect(Collectors.toList()));
        });

        binding.distanceSeekBar.addOnChangeListener((slider, value, fromUser) -> {
            binding.distance.setText((int) value + " km");
            dataTransfer.setPrefferedDistance(dataTransfer.getUUID(), (int) value);
        });
        load();
    }

    public void load() {
        dataTransfer.getPreferences(dataTransfer.getUUID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (binding != null) {
                    UserPreferences userPreferences = task.getResult().getValue(UserPreferences.class);
                    this.binding.distanceSeekBar.setValue(userPreferences.getMaxDistance());
                    this.binding.ageRangeSlider.setValues(
                            userPreferences.getMinAge().floatValue(),
                            userPreferences.getMaxAge().floatValue()
                    );
                    binding.age.setText(userPreferences.getMinAge().intValue() + "-" + userPreferences.getMaxAge().intValue());
                    binding.distance.setText(userPreferences.getMaxDistance().intValue() + " km");
                }
            }
        });

    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        mainActivityViewModel.setActionBarVisible(false);
        mainActivityViewModel.setBottomBarVisible(false);
        mainActivityViewModel.setProfilePicture(null);
        navController.navigate(R.id.loadingFragment, null, navOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
