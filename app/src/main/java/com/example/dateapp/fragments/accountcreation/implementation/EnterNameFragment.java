package com.example.dateapp.fragments.accountcreation.implementation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.dateapp.R;
import com.example.dateapp.databinding.FragmentEnterNameBinding;
import com.example.dateapp.fragments.accountcreation.AccountCreation;
import com.example.dateapp.viewmodels.AuthenticationViewModel;


public class EnterNameFragment extends AccountCreation {

    public static final String TAG="EnterNameFragment";
    private static final Integer ProgressBarProgress = 1;
    private FragmentEnterNameBinding binding;


    public EnterNameFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterNameBinding.inflate(inflater, container, false);



        binding.next.setOnClickListener(this::moveNext);

        binding.nameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
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
        binding=null;
    }

    @Override
    protected void load() {
        this.binding.nameTextInputEditText.setText(this.getAuthenticationViewModel().getName());
    }

    @Override
    protected void save() {
        this.getAuthenticationViewModel().setName(binding.nameTextInputEditText.getText().toString());
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterAgeFragment);
    }


    @Override
    protected Boolean validate() {
        if(binding.nameTextInputEditText.getText().toString().length()>0) {
            this.setValid(true);
            binding.next.setEnabled(this.getValid());
            return this.getValid();
        }
        this.setValid(false);
        binding.next.setEnabled(this.getValid());
        return this.getValid();
    }

}
