package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.navigation.Navigation;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentEnterAllowGpsBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreationFragment;
import pl.jhonylemon.dateapp.utils.GpsChecker;


public class EnterAllowGpsFragment extends AccountCreationFragment {

    public static final String TAG="EnterAllowGpsFragment";
    private static final Integer ProgressBarProgress = 3;
    private FragmentEnterAllowGpsBinding binding;

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
        createDocument();
        askGpsPermission(null);
    }

    private void createDocument(){
        this.dataTransfer.setDocument(dataTransfer.getUUID()).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                createDocument();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    private void askGpsPermission(View view)
    {
            mainActivityViewModel.setEnableLocation(true);
            validateAndEnableNext();
    }

    @Override
    protected Task<DataSnapshot> load() {

        return null;
    }

    @Override
    protected Task<Void> save() {

        return null;
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterGenderFragment);
    }


    @Override
    protected Boolean validate() {
        if (this.mainActivityViewModel.getMainActivity().startGps()){
            this.setValid(true);
            binding.next.setEnabled(this.getValid());
            return this.getValid();
        }
        this.setValid(false);
        binding.next.setEnabled(this.getValid());
        return this.getValid();
    }

    @Override
    protected Boolean validateAndEnableNext() {
        boolean valid = validate();
        if(valid){
            binding.next.setEnabled(getValid());
        }
        return valid;
    }
}
