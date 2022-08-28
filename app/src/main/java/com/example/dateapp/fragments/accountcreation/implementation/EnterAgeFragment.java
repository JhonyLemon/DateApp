package com.example.dateapp.fragments.accountcreation.implementation;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.navigation.Navigation;

import com.example.dateapp.R;
import com.example.dateapp.databinding.FragmentEnterAgeBinding;
import com.example.dateapp.fragments.accountcreation.AccountCreation;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EnterAgeFragment extends AccountCreation {

    public static final String TAG="EnterAgeFragment";
    private static final Integer ProgressBarProgress = 2;
    private FragmentEnterAgeBinding binding;

    private final Long millisecondDayRatio =86400000L;

    private MaterialDatePicker datePicker;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
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

        datePicker =MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.enter_age_hint))
                .setSelection(this.getAuthenticationViewModel()
                        .getBirthdayDate()==null ?
                        MaterialDatePicker.todayInUtcMilliseconds()
                        :
                        (this.getAuthenticationViewModel()
                        .getBirthdayDate()
                        .toEpochDay()*millisecondDayRatio)
                )
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setCalendarConstraints(new CalendarConstraints
                        .Builder()
                        .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                )
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> getDatePickerDate((Long)selection));

        binding.ageTextInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !datePicker.isAdded()) {
                datePicker.show(getChildFragmentManager(), datePicker.getTag());
            }
        });
        binding.ageTextInputEditText.setOnClickListener(v ->{
            if(!datePicker.isAdded()) {
                datePicker.show(getChildFragmentManager(), datePicker.getTag());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    private void getDatePickerDate(Long epochMiliSeconds){
        binding.ageTextInputEditText.setText(LocalDate
                .ofEpochDay(epochMiliSeconds/millisecondDayRatio)
                .format(formatter)
        );
        if (validate())
            save();
    }

    private LocalDate getLocalDateFromString(String date){
        try {
            return LocalDate.parse(date, formatter);
        }catch (DateTimeParseException e){
            Log.e(TAG,e.getMessage());
            throw new RuntimeException("Failed to parse date: "+e.getMessage());
        }
    }


    @Override
    protected void load() {
        binding.ageTextInputEditText
                .setText(
                        this.getAuthenticationViewModel()
                                .getBirthdayDate()==null ?
                                ""
                                :
                                this.getAuthenticationViewModel()
                                        .getBirthdayDate()
                                        .format(formatter)
                );
    }


    @Override
    protected void save() {
        this.getAuthenticationViewModel()
                .setBirthdayDate(getLocalDateFromString(
                        binding
                        .ageTextInputEditText
                        .getText()
                        .toString()
                        )
                );
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterAllowGpsFragment);
    }

    @Override
    protected Boolean validate() {


        binding
                .ageTextInputEditText
                .getText().toString();
        if(LocalDate.now().minusYears(18)
                .compareTo(
                        binding.ageTextInputEditText.getText().toString().equals("")
                                ?
                        LocalDate.now()
                                :
                        getLocalDateFromString(binding.ageTextInputEditText.getText().toString())
                )>=0
        ) {
            this.setValid(true);
            binding.next.setEnabled(this.getValid());
            return this.getValid();
        }
        this.setValid(false);
        binding.next.setEnabled(this.getValid());
        return this.getValid();
    }
}
