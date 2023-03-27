package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.adapters.RecyclerChipsViewAdapter;
import pl.jhonylemon.dateapp.databinding.FragmentEnterWhoToShowBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreationFragment;
import pl.jhonylemon.dateapp.mappers.ChipItemMapper;
import pl.jhonylemon.dateapp.models.ChipItem;
import pl.jhonylemon.dateapp.entity.UserPreferences;
import pl.jhonylemon.dateapp.utils.DataTransfer;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnterWhoToShowFragment extends AccountCreationFragment {

    public static final String TAG="EnterWhoToShowFragment";
    private static final String GENDER="GENDER";
    private static final String PASSION="PASSION";
    private static final String ORIENTATION="ORIENTATION";
    private static final Integer ProgressBarProgress = 9;
    private FragmentEnterWhoToShowBinding binding;
    private NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.enterWhoToShowFragment,true).build();

    RecyclerChipsViewAdapter genderAdapter;
    RecyclerChipsViewAdapter passionsAdapter;
    RecyclerChipsViewAdapter orientationAdapter;

    List<ChipItem> genders;
    List<ChipItem> orientations;
    List<ChipItem> passions;

    private UserPreferences userPreferences;

    private List<String> gendersString;
    private List<String> orientationsString;
    private List<String> passionsString;

    private DataTransfer dataTransfer;

    public EnterWhoToShowFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterWhoToShowBinding.inflate(inflater, container, false);

        genders = new ArrayList<>();
        orientations = new ArrayList<>();
        passions = new ArrayList<>();

        dataTransfer=new DataTransfer();

        binding.previous.setOnClickListener(this::movePrevious);
        binding.next.setOnClickListener(this::moveNext);

        gendersString = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.genders)));
        orientationsString = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sex_orientations)));
        passionsString = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.passions)));

        getParentFragmentManager().setFragmentResultListener(ListSelectionFragment.TAG, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Integer id = bundle.getInt(ListSelectionFragment.RETURN_ID);
                String caller = bundle.getString(ListSelectionFragment.CALLER_ID);
                Task<DataSnapshot> loadTask = load();
                switch (caller){
                    case GENDER:
                        loadTask.continueWithTask(task -> {
                            if(task.isSuccessful()){
                                genders.add(new ChipItem(gendersString.get(id), R.drawable.ic_baseline_cancel_24,id));
                                return dataTransfer.setPrefferedGender(dataTransfer.getUUID(), ChipItemMapper.mapListChipItemToListInteger(genders, gendersString));
                            }else{
                                return Tasks.forCanceled();
                            }
                        }).addOnCompleteListener(task -> {
                            if(binding!=null) {
                                validateAndEnableNext();
                            }
                        });
                        break;
                    case PASSION:
                        loadTask.continueWithTask(task -> {
                            if(task.isSuccessful()){
                                passions.add(new ChipItem(passionsString.get(id), R.drawable.ic_baseline_cancel_24,id));
                                return dataTransfer.setPrefferedPassions(dataTransfer.getUUID(), ChipItemMapper.mapListChipItemToListInteger(passions, passionsString));
                            }else{
                                return Tasks.forCanceled();
                            }
                        }).addOnCompleteListener(task -> {
                            if(binding!=null) {
                                validateAndEnableNext();
                            }
                        });
                        break;
                    case ORIENTATION:
                        loadTask.continueWithTask(task -> {
                            if(task.isSuccessful()){
                                orientations.add(new ChipItem(orientationsString.get(id), R.drawable.ic_baseline_cancel_24,id));
                                return dataTransfer.setPrefferedOrientation(dataTransfer.getUUID(), ChipItemMapper.mapListChipItemToListInteger(orientations, orientationsString));
                            }else{
                                return Tasks.forCanceled();
                            }
                        }).addOnCompleteListener(task -> {
                            if(binding!=null) {
                                validateAndEnableNext();
                            }
                        });
                        break;
                }
            }
        });

        genderAdapter = new RecyclerChipsViewAdapter(genders, getContext(), (value, position) -> {
            if(position==genders.size()){//
                ArrayList<String> withoutSelected= new ArrayList<>(gendersString);
                Bundle args = new Bundle();

                args.putStringArrayList(ListSelectionFragment.FULL_LIST, (ArrayList<String>) gendersString);
                genders.forEach(item->{
                    withoutSelected.remove(item.getText());
                });

                args.putStringArrayList(ListSelectionFragment.LIST_WITHOUT_SELECTED,withoutSelected);
                args.putString(ListSelectionFragment.CALLER_ID,GENDER);
                getNavController().navigate(R.id.listSelectionFragment,args);
            }else{
                genders.remove(position.intValue());
                genderAdapter.UpdateList(genders);
                dataTransfer.setPrefferedGender(dataTransfer.getUUID(), ChipItemMapper.mapListChipItemToListInteger(genders, gendersString))
                        .continueWithTask(task->load());
            }
        });

        orientationAdapter = new RecyclerChipsViewAdapter(orientations, getContext(), (value, position) -> {
            if(position==orientations.size()){//
                ArrayList<String> withoutSelected= new ArrayList<>(orientationsString);
                Bundle args = new Bundle();

                args.putStringArrayList(ListSelectionFragment.FULL_LIST, (ArrayList<String>) orientationsString);
                orientations.forEach(item->{
                    withoutSelected.remove(item.getText());
                });

                args.putStringArrayList(ListSelectionFragment.LIST_WITHOUT_SELECTED,withoutSelected);
                args.putString(ListSelectionFragment.CALLER_ID,ORIENTATION);
                getNavController().navigate(R.id.listSelectionFragment,args);
            }else{
                orientations.remove(position.intValue());
                orientationAdapter.UpdateList(orientations);
                dataTransfer.setPrefferedOrientation(dataTransfer.getUUID(), ChipItemMapper.mapListChipItemToListInteger(orientations, orientationsString))
                        .continueWithTask(task->load());
            }
        });

        passionsAdapter = new RecyclerChipsViewAdapter(passions, getContext(), new RecyclerChipsViewAdapter.OnRecyclerItemClick() {
            @Override
            public void onItemClick(String value, Integer position) {
                if(position==passions.size()){
                    ArrayList<String> withoutSelected= new ArrayList<>(passionsString);
                    Bundle args = new Bundle();

                    args.putStringArrayList(ListSelectionFragment.FULL_LIST, (ArrayList<String>) passionsString);
                    passions.forEach(item->{
                        withoutSelected.remove(item.getText());
                    });

                    args.putStringArrayList(ListSelectionFragment.LIST_WITHOUT_SELECTED,withoutSelected);
                    args.putString(ListSelectionFragment.CALLER_ID,PASSION);
                    getNavController().navigate(R.id.listSelectionFragment,args);
                }else{
                    passions.remove(position.intValue());
                    passionsAdapter.UpdateList(passions);
                    dataTransfer.setPrefferedOrientation(dataTransfer.getUUID(), ChipItemMapper.mapListChipItemToListInteger(passions, passionsString))
                            .continueWithTask(task->load());
                }
            }
        });

        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setNavController(Navigation.findNavController(view));
        this.getAuthenticationViewModel().setFragment(ProgressBarProgress);

        FlexboxLayoutManager layoutManagerGender = new FlexboxLayoutManager(getContext());
        layoutManagerGender.setFlexDirection(FlexDirection.ROW);
        layoutManagerGender.setFlexWrap(FlexWrap.WRAP);
        layoutManagerGender.setJustifyContent(JustifyContent.CENTER);
        layoutManagerGender.setAlignItems(AlignItems.CENTER);

        FlexboxLayoutManager layoutManagerPassions = new FlexboxLayoutManager(getContext());
        layoutManagerPassions.setFlexDirection(FlexDirection.ROW);
        layoutManagerPassions.setFlexWrap(FlexWrap.WRAP);
        layoutManagerPassions.setJustifyContent(JustifyContent.CENTER);
        layoutManagerPassions.setAlignItems(AlignItems.CENTER);

        FlexboxLayoutManager layoutManagerOrientation = new FlexboxLayoutManager(getContext());
        layoutManagerOrientation.setFlexDirection(FlexDirection.ROW);
        layoutManagerOrientation.setFlexWrap(FlexWrap.WRAP);
        layoutManagerOrientation.setJustifyContent(JustifyContent.CENTER);
        layoutManagerOrientation.setAlignItems(AlignItems.CENTER);

        binding.genderGridView.setAdapter(genderAdapter);
        binding.passionsGridView.setAdapter(passionsAdapter);
        binding.orientationGridView.setAdapter(orientationAdapter);

        binding.genderGridView.setLayoutManager(layoutManagerGender);
        binding.passionsGridView.setLayoutManager(layoutManagerPassions);
        binding.orientationGridView.setLayoutManager(layoutManagerOrientation);

        binding.ageRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> age=slider.getValues();
            binding.age.setText(age.get(0).intValue() +"-"+age.get(1).intValue());
            dataTransfer.setPrefferedAge(dataTransfer.getUUID(),age.stream().map(Float::intValue).collect(Collectors.toList()))
                    .addOnCompleteListener(task -> {
                        validateAndEnableNext();
                    });
        });

        binding.distanceSeekBar.addOnChangeListener((slider, value, fromUser) -> {
            binding.distance.setText((int)value+" km");
            dataTransfer.setPrefferedDistance(dataTransfer.getUUID(),(int)value).addOnCompleteListener(task->{
               validateAndEnableNext();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    private void showSnackbar(String msg) {
        Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected Task<DataSnapshot> load() {
        return dataTransfer.getPreferences(dataTransfer.getUUID()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(binding!=null) {
                    userPreferences = Optional.ofNullable(task.getResult().getValue(UserPreferences.class)).orElse(new UserPreferences());
                    passions = ChipItemMapper.mapListIntegerToListChipItem(userPreferences.getPassions().stream().map(Integer::longValue).collect(Collectors.toList()), passionsString);
                    genders = ChipItemMapper.mapListIntegerToListChipItem(userPreferences.getGenderId().stream().map(Integer::longValue).collect(Collectors.toList()), gendersString);
                    orientations = ChipItemMapper.mapListIntegerToListChipItem(userPreferences.getOrientationId().stream().map(Integer::longValue).collect(Collectors.toList()), orientationsString);
                    passionsAdapter.UpdateList(passions);
                    genderAdapter.UpdateList(genders);
                    orientationAdapter.UpdateList(orientations);
                    binding.ageRangeSlider.setValues(
                            List.of(
                                    userPreferences.getMinAge().floatValue(),
                                    userPreferences.getMaxAge().floatValue()
                            )
                    );
                    binding.distanceSeekBar.setValue(userPreferences.getMaxDistance().floatValue());
                    binding.age.setText(binding.ageRangeSlider.getValues().get(0).intValue() + "-" + binding.ageRangeSlider.getValues().get(1).intValue());
                    binding.distance.setText(binding.distanceSeekBar.getValue() + " km");
                    validateAndEnableNext();
                }
            }else{
                load();
            }
       });
    }


    @Override
    protected Task<Void> save() {
        userPreferences.setMaxDistance((int)binding.distanceSeekBar.getValue());
        userPreferences.setMinAge(binding.ageRangeSlider.getValues().get(0).intValue());
        userPreferences.setMaxAge(binding.ageRangeSlider.getValues().get(1).intValue());

        userPreferences.setPassions(ChipItemMapper.mapListChipItemToListInteger(passions,passionsString));
        userPreferences.setGenderId(ChipItemMapper.mapListChipItemToListInteger(genders,gendersString));
        userPreferences.setOrientationId(ChipItemMapper.mapListChipItemToListInteger(orientations,orientationsString));

        dataTransfer.setPreferences(dataTransfer.getUUID(), userPreferences).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(binding!=null) {
                    validateAndEnableNext();
                }
            }else{
                save();
            }
        });
        return null;
    }

    @Override
    protected void moveNext(View view) {
        moveToMainActivity();
    }

    @Override
    protected Boolean validate() {
        setValid(true);
        return getValid();
    }

    @Override
    protected Boolean validateAndEnableNext() {
        boolean valid = validate();
        if(valid){
            binding.next.setEnabled(getValid());
        }
        return valid;
    }

    private void moveToMainActivity()
    {
        this.mainActivityViewModel.setBottomBarVisible(true);
        this.mainActivityViewModel.setActionBarVisible(true);
        this.mainActivityViewModel.setProgressBarVisible(false);
        this.dataTransfer.setNewUser(dataTransfer.getUUID(), false).addOnCompleteListener(task -> {
            this.mainActivityViewModel.getMainActivity().addProfilePhotoListener();
            navController.navigate(R.id.swipeFragment,null,navOptions);
        });
    }
}
