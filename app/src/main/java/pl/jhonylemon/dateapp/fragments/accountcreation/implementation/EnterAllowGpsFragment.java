package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentEnterAllowGpsBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreation;


public class EnterAllowGpsFragment extends AccountCreation {

    public static final String TAG="EnterAllowGpsFragment";
    private static final Integer ProgressBarProgress = 3;
    private FragmentEnterAllowGpsBinding binding;

    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if(result) {
                    Log.e("TAG", "onActivityResult: PERMISSION GRANTED");
                    binding.next.setEnabled(true);
                } else {
                    Log.e("TAG", "onActivityResult: PERMISSION DENIED");
                    binding.next.setEnabled(false);
                }
            });


    public EnterAllowGpsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterAllowGpsBinding.inflate(inflater, container, false);

        binding.next.setOnClickListener(this::moveNext);
        binding.previous.setOnClickListener(this::movePrevious);
        binding.gps.setOnClickListener(this::askGpsPermission);

        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setNavController(Navigation.findNavController(view));
        this.getAuthenticationViewModel().setFragment(ProgressBarProgress);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    private void askGpsPermission(View view)
    {
        mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    protected void load() {

    }

    @Override
    protected void save() {

    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterGenderFragment);
    }


    @Override
    protected Boolean validate() {
        if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            this.setValid(true);
            binding.next.setEnabled(this.getValid());
            return this.getValid();
        }
        this.setValid(false);
        binding.next.setEnabled(this.getValid());
        return this.getValid();
    }
}
