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
import pl.jhonylemon.dateapp.databinding.FragmentEnterDescriptionBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreationFragment;
import pl.jhonylemon.dateapp.utils.DataTransfer;

public class EnterDescriptionFragment extends AccountCreationFragment {

    public static final String TAG = "EnterDescriptionFragment";
    private static final Integer ProgressBarProgress = 7;
    private FragmentEnterDescriptionBinding binding;

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
        dataTransfer = new DataTransfer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    protected Task<DataSnapshot> load() {
        return dataTransfer.getDescription(dataTransfer.getUUID()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(binding!=null){
                    binding.descriptionTextInputEditText.setText(task.getResult().getValue(String.class));
                    validateAndEnableNext();
                }
            }else{
                load();
            }
        });
    }

    @Override
    protected Task<Void> save() {
        return dataTransfer.setDescription(
                dataTransfer.getUUID(),
                binding.descriptionTextInputEditText.getText().toString()
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
        this.getNavController().navigate(R.id.enterPhotosFragment);
    }

    @Override
    protected Boolean validate() {
        if(binding.descriptionTextInputEditText.getText()!=null && binding.descriptionTextInputEditText.getText().toString().length()>0) {
            this.setValid(true);
            return this.getValid();
        }
        this.setValid(false);
        binding.next.setEnabled(true);
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
