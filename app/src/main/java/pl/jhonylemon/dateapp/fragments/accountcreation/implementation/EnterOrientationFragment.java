package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import pl.jhonylemon.dateapp.AuthenticationActivity;
import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentEnterOrientationBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreation;
import pl.jhonylemon.dateapp.fragments.accountcreation.ListSelectionFragment;
import pl.jhonylemon.dateapp.viewmodels.AuthenticationViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnterOrientationFragment extends AccountCreation {

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
                if(validate())
                    save();
                load();
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
    protected void load() {
        orientationID=this.getAuthenticationViewModel().getOrientation();
        if(orientationID!=null){
            binding.selectOrientation.setText(orientations.get(orientationID));
        }
    }

    @Override
    protected void save() {
        this.getAuthenticationViewModel().setOrientation(orientationID);
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterPassionsFragment);
    }


    @Override
    protected Boolean validate() {
        if(orientationID!=null) {
            this.setValid(true);
            binding.next.setEnabled(this.getValid());
            return this.getValid();
        }
        this.setValid(false);
        binding.next.setEnabled(this.getValid());
        return this.getValid();
    }
}
