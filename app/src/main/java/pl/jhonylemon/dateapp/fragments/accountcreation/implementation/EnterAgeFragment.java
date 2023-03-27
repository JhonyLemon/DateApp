package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.navigation.Navigation;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentEnterAgeBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreationFragment;
import pl.jhonylemon.dateapp.utils.HelperFunctions;

import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class EnterAgeFragment extends AccountCreationFragment {

    public static final String TAG = "EnterAgeFragment";
    private static final Integer ProgressBarProgress = 2;
    private FragmentEnterAgeBinding binding;

    private final Long millisecondDayRatio = 86400000L;

    private MaterialDatePicker datePicker;
    DateTimeFormatter formatter = HelperFunctions.dateTimeFormatter();

    public EnterAgeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterAgeBinding.inflate(inflater, container, false);

        binding.next.setOnClickListener(this::moveNext);
        binding.previous.setOnClickListener(this::movePrevious);

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

    private void getDatePickerDate(Long epochMiliSeconds) {
        LocalDate localDate;

        binding.ageTextInputEditText.setText(LocalDate
                .ofEpochDay(epochMiliSeconds / millisecondDayRatio)
                .format(formatter)
        );
        if (validate())
            save();
    }

    @Override
    protected Task<DataSnapshot> load() {
        return dataTransfer.getBirthdayDate(dataTransfer.getUUID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(binding!=null) {
                    binding.ageTextInputEditText.setText(task.getResult().getValue(String.class));
                    datePicker = MaterialDatePicker.Builder.datePicker()
                            .setTitleText(getString(R.string.enter_age_hint))
                            .setSelection(
                                    Optional.ofNullable(HelperFunctions.stringToLocalDate(binding.ageTextInputEditText.getText().toString()))
                                            .map(date -> date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
                                            .orElse(MaterialDatePicker.todayInUtcMilliseconds())
                            )
                            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                            .setCalendarConstraints(new CalendarConstraints
                                    .Builder()
                                    .setStart(LocalDateTime.now()
                                            .minus(120, ChronoUnit.YEARS)
                                            .atZone(ZoneId.systemDefault())
                                            .toInstant()
                                            .toEpochMilli()
                                    )
                                    .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                                    .build()
                            )
                            .build();
                    datePicker.addOnPositiveButtonClickListener(selection -> getDatePickerDate((Long) selection));

                    binding.ageTextInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus && !datePicker.isAdded()) {
                            datePicker.show(getChildFragmentManager(), datePicker.getTag());
                        }
                    });
                    binding.ageTextInputEditText.setOnClickListener(v -> {
                        if (!datePicker.isAdded()) {
                            datePicker.show(getChildFragmentManager(), datePicker.getTag());
                        }
                    });
                    validateAndEnableNext();
                }
            } else {
                load();
            }
        });
    }


    @Override
    protected Task<Void> save() {
        return dataTransfer.setBirthdayDate(
                dataTransfer.getUUID(),
                binding.ageTextInputEditText.getText().toString()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(binding!=null) {
                    validateAndEnableNext();
                }
            } else {
                save();
            }
        });
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterAllowGpsFragment);
    }

    @Override
    protected Boolean validate() {
        if (
                binding.ageTextInputEditText.getText() != null &&
                !binding.ageTextInputEditText.getText().toString().isEmpty() &&
                        HelperFunctions.ageBetween(
                                HelperFunctions.getAgeFromBirthdayDate(
                                        HelperFunctions.stringToLocalDate(
                                                binding.ageTextInputEditText.getText().toString()
                                        )
                                ),
                                18,
                                null
                        )
        ) {
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
