package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentEnterNameBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreationFragment;


public class EnterNameFragment extends AccountCreationFragment {

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
    protected Task<DataSnapshot> load() {
        return dataTransfer.getName(dataTransfer.getUUID()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(binding!=null) {
                    binding.nameTextInputEditText.setText(task.getResult().getValue(String.class));
                    validateAndEnableNext();
                }
            }else{
                load();
            }
        });
    }

    @Override
    protected Task<Void> save() {
        return dataTransfer.setName(
                dataTransfer.getUUID(),
                binding.nameTextInputEditText.getText().toString()
        ).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(binding!=null) {
                    binding.next.setEnabled(getValid());
                }
            }else {
                save();
            }
        });
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterAgeFragment);
    }


    @Override
    protected Boolean validate() {
        if(binding.nameTextInputEditText.getText()!=null && binding.nameTextInputEditText.getText().toString().length()>0) {
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
