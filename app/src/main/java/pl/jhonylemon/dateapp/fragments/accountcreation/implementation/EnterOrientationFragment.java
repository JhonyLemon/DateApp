package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentEnterOrientationBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreationFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnterOrientationFragment extends AccountCreationFragment {

    public static final String TAG="EnterOrientationFragment";
    private static final Integer ProgressBarProgress = 5;
    private FragmentEnterOrientationBinding binding;

    private List<String> orientations;
    private Integer orientationID=null;

    public EnterOrientationFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterOrientationBinding.inflate(inflater, container, false);

        orientations=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sex_orientations)));

        binding.next.setOnClickListener(this::moveNext);
        binding.previous.setOnClickListener(this::movePrevious);
        binding.selectOrientation.setOnClickListener(this::selectOrientation);

        getParentFragmentManager().setFragmentResultListener(ListSelectionFragment.TAG, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                orientationID = bundle.getInt(ListSelectionFragment.RETURN_ID);
                save().continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return load();
                    }
                    return Tasks.forCanceled();
                });
            }
        });
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

    private void selectOrientation(View view){
        moveToListSelection();
    }

    @Override
    protected void moveToListSelection() {
        ArrayList<String> withoutSelected= new ArrayList<>(orientations);
        Bundle args = new Bundle();
        args.putStringArrayList(ListSelectionFragment.FULL_LIST, (ArrayList<String>) orientations);
        if (orientationID!=null)
            withoutSelected.remove(orientationID);
        args.putStringArrayList(ListSelectionFragment.LIST_WITHOUT_SELECTED,withoutSelected);
        this.getNavController().navigate(R.id.listSelectionFragment,args);
    }

    @Override
    protected Task<DataSnapshot> load() {
        return dataTransfer.getOrientationId(dataTransfer.getUUID()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(binding!=null) {
                    orientationID = task.getResult().getValue(Integer.class);
                    if (orientationID != null) {
                        binding.selectOrientation.setText(orientations.get(orientationID));
                    }
                    validateAndEnableNext();
                }
            }else{
                load();
            }
        });
    }

    @Override
    protected Task<Void> save() {
        return dataTransfer.setOrientationId(dataTransfer.getUUID(), orientationID).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(binding!=null) {
                    binding.next.setEnabled(this.getValid());
                }
            }else{
                save();
            }
        });
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterPassionsFragment);
    }


    @Override
    protected Boolean validate() {
        if(orientationID!=null) {
            this.setValid(true);
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
