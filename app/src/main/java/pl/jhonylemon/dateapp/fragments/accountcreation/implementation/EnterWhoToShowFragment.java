package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.adapters.RecyclerChipsViewAdapter;
import pl.jhonylemon.dateapp.databinding.FragmentEnterWhoToShowBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreation;
import pl.jhonylemon.dateapp.fragments.accountcreation.ListSelectionFragment;
import pl.jhonylemon.dateapp.mappers.ChipItemMapper;
import pl.jhonylemon.dateapp.mappers.ChipItemMapperImpl;
import pl.jhonylemon.dateapp.models.ChipItem;
import pl.jhonylemon.dateapp.models.UserPreferences;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnterWhoToShowFragment extends AccountCreation {

    public static final String TAG="EnterWhoToShowFragment";
    private static final String GENDER="GENDER";
    private static final String PASSION="PASSION";
    private static final String ORIENTATION="ORIENTATION";
    private static final Integer ProgressBarProgress = 9;
    private FragmentEnterWhoToShowBinding binding;

    public static final ChipItemMapper mapper = new ChipItemMapperImpl();

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

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public EnterWhoToShowFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterWhoToShowBinding.inflate(inflater, container, false);

        genders = new ArrayList<>();
        orientations = new ArrayList<>();
        passions = new ArrayList<>();

        db=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

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
                switch (caller){
                    case GENDER:
                        genders.add(new ChipItem(gendersString.get(id), R.drawable.ic_baseline_cancel_24,id));
                        break;
                    case PASSION:
                        passions.add(new ChipItem(passionsString.get(id), R.drawable.ic_baseline_cancel_24,id));
                        break;
                    case ORIENTATION:
                        orientations.add(new ChipItem(orientationsString.get(id), R.drawable.ic_baseline_cancel_24,id));
                        break;
                }
                if(validate())
                    save();
                load();
            }
        });

        genderAdapter = new RecyclerChipsViewAdapter(genders, getContext(), new RecyclerChipsViewAdapter.OnRecyclerItemClick() {
            @Override
            public void onItemClick(String value, Integer position) {
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
                    if(validate())
                        save();
                    genderAdapter.UpdateList(genders);
                }
            }
        });

        orientationAdapter = new RecyclerChipsViewAdapter(orientations, getContext(), new RecyclerChipsViewAdapter.OnRecyclerItemClick() {
            @Override
            public void onItemClick(String value, Integer position) {
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
                    if(validate())
                        save();
                    orientationAdapter.UpdateList(orientations);
                }
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
                    if(validate())
                        save();
                    passionsAdapter.UpdateList(passions);
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
            if(validate())
                save();
        });

        binding.distanceSeekBar.addOnChangeListener((slider, value, fromUser) -> {
            binding.distance.setText((int)value+" km");
            if(validate())
                save();
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
    protected void load() {
      userPreferences=this.getAuthenticationViewModel().getPreferences();

      passions = mapper.mapListIntegerToListChipItem(userPreferences.getPassions(),passionsString);
      genders = mapper.mapListIntegerToListChipItem(userPreferences.getGenderId(),gendersString);
      orientations = mapper.mapListIntegerToListChipItem(userPreferences.getOrientationId(),orientationsString);

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

      binding.age.setText(binding.ageRangeSlider.getValues().get(0).intValue() +"-"+binding.ageRangeSlider.getValues().get(1).intValue());
      binding.distance.setText(String.valueOf(binding.distanceSeekBar.getValue())+" km");
    }


    @Override
    protected void save() {
        userPreferences.setMaxDistance((int)binding.distanceSeekBar.getValue());
        userPreferences.setMinAge(binding.ageRangeSlider.getValues().get(0).intValue());
        userPreferences.setMaxAge(binding.ageRangeSlider.getValues().get(1).intValue());

        userPreferences.setPassions(mapper.mapListChipItemToListInteger(passions,passionsString));
        userPreferences.setGenderId(mapper.mapListChipItemToListInteger(genders,gendersString));
        userPreferences.setOrientationId(mapper.mapListChipItemToListInteger(orientations,orientationsString));

        this.getAuthenticationViewModel().setPreferences(userPreferences);
    }

    @Override
    protected void moveNext(View view) {
        StorageReference ref=storageReference.child(this.getAuthenticationViewModel().getUserUID());

        for(Integer i=0;i<this.getAuthenticationViewModel().getPhotos().size();i++){
            Bitmap bitmap = this.getAuthenticationViewModel().getPhotos().get(i);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = ref.child(i.toString()).putBytes(data);
            uploadTask
                    .addOnFailureListener(exception -> showSnackbar("Failure"))
                    .addOnSuccessListener(taskSnapshot -> showSnackbar("Sent"));
        }
        db
                .collection("users")
                .document(this.getAuthenticationViewModel().getUserUID())
                .set(this.getAuthenticationViewModel().getUserData().getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            showSnackbar("Sent");
                        else{
                            showSnackbar("Failure");
                        }
                    }
                });
        //TODO send data to firestore
    }


    @Override
    protected Boolean validate() {
        return true;
    }
}
