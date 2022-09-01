package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import pl.jhonylemon.dateapp.AuthenticationActivity;
import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentEnterDescriptionBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreation;
import pl.jhonylemon.dateapp.viewmodels.AuthenticationViewModel;

import java.util.ArrayList;
import java.util.Arrays;

public class EnterDescriptionFragment extends AccountCreation {

    public static final String TAG = "EnterDescriptionFragment";
    private static final Integer ProgressBarProgress = 7;
    private FragmentEnterDescriptionBinding binding;
    private NavController navController;
    private Integer orientationID = null;
    AuthenticationViewModel authenticationViewModel;


    public EnterDescriptionFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterDescriptionBinding.inflate(inflater, container, false);


        binding.next.setOnClickListener(this::moveNext);
        binding.previous.setOnClickListener(this::movePrevious);

        binding.descriptionTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
               if(validate())
                   save();
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
        binding = null;
    }

    @Override
    protected void load() {
       binding.descriptionTextInputEditText.setText(this.getAuthenticationViewModel().getDescription());
    }

    @Override
    protected void save() {
        this.getAuthenticationViewModel().setDescription(binding.descriptionTextInputEditText.getText().toString());
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterPhotosFragment);
    }

    @Override
    protected Boolean validate() {
        if(binding.descriptionTextInputEditText.getText().toString().length()>0) {
            this.setValid(true);
            binding.next.setEnabled(true);
            return this.getValid();
        }
        this.setValid(false);
        binding.next.setEnabled(true);
        return this.getValid();
    }
}
