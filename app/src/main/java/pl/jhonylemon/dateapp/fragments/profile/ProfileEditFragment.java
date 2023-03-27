package pl.jhonylemon.dateapp.fragments.profile;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.adapters.GridViewPhotosAdapter;
import pl.jhonylemon.dateapp.adapters.RecyclerChipsViewAdapter;
import pl.jhonylemon.dateapp.databinding.FragmentProfileEditBinding;
import pl.jhonylemon.dateapp.databinding.GridviewPhotoItemBinding;
import pl.jhonylemon.dateapp.entity.UserDetails;
import pl.jhonylemon.dateapp.fragments.BaseFragment;
import pl.jhonylemon.dateapp.fragments.CropImageFragment;
import pl.jhonylemon.dateapp.fragments.accountcreation.implementation.ListSelectionFragment;
import pl.jhonylemon.dateapp.mappers.ChipItemMapper;
import pl.jhonylemon.dateapp.models.ChipItem;
import pl.jhonylemon.dateapp.entity.UserData;
import pl.jhonylemon.dateapp.utils.DataTransfer;
import pl.jhonylemon.dateapp.utils.HelperFunctions;
import pl.jhonylemon.dateapp.utils.PhotoTransfer;

public class ProfileEditFragment extends BaseFragment {

    public static final String TAG="ProfileEditFragment";
    private FragmentProfileEditBinding binding;
    private DataTransfer dataTransfer = new DataTransfer();
    private PhotoTransfer photoTransfer = new PhotoTransfer();

    public static final int PICK_IMAGE = 1;

    private String ext;

    private ActivityResultLauncher<String> mGetContent;
    private final MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

    private final Long millisecondDayRatio =86400000L;
    private MaterialDatePicker datePicker;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String GENDER="GENDER";
    private static final String PASSION="PASSION";
    private static final String ORIENTATION="ORIENTATION";

    private List<String> passions;
    private List<ChipItem> passionChips;
    private RecyclerChipsViewAdapter adapter;

    private List<String> genders;
    private Integer genderID=null;

    private List<String> orientations;
    private Integer orientationID=null;

    private List<String> uris = new ArrayList<>();
    private List<GridviewPhotoItemBinding> photoItems;

    public ProfileEditFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false);

        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if(result!=null && getContext()!=null) {
                ContentResolver resolver =getContext().getContentResolver();
                if(resolver!=null){
                    Bundle args = new Bundle();
                    args.putParcelable(CropImageFragment.INPUT,result);
                    navController.navigate(R.id.cropImageFragment,args);
                }

            }

        });

        getParentFragmentManager().setFragmentResultListener(CropImageFragment.TAG, this, (requestKey, result) -> {
            Bitmap image = result.getParcelable(CropImageFragment.RESULT);
            Uri imageUri = result.getParcelable(CropImageFragment.INPUT);
            if(result!=null && getContext()!=null) {
                ContentResolver resolver =getContext().getContentResolver();
                if(resolver!=null){
                    ext = mimeTypeMap.getExtensionFromMimeType(resolver.getType(imageUri));
                    AtomicReference<List<String>> atomicReference = new AtomicReference<>();
                    load().continueWithTask(task -> {
                        if(task.isSuccessful()){
                            UserDetails userDetails = Optional.ofNullable(task.getResult().getValue(UserDetails.class)).orElse(new UserDetails());
                            atomicReference.set(userDetails.getUserPhotos().getPhotos());
                            return photoTransfer.postPhoto(dataTransfer.getUUID(), image, ext);
                        }
                        return Tasks.forCanceled();
                    }).continueWithTask(task -> {
                        if(task.isSuccessful()){
                            List<String> urls = atomicReference.get();
                            urls.add(task.getResult());
                            atomicReference.set(urls);
                            return dataTransfer.setUserPhotos(dataTransfer.getUUID(), atomicReference.get());
                        }
                        return Tasks.forCanceled();
                    }).addOnCompleteListener(task -> {
                        load();
                    });
                }

            }

        });

        passions=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.passions)));
        passionChips = new ArrayList<>();

        getParentFragmentManager().setFragmentResultListener(ListSelectionFragment.TAG, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                passionChips.add(new ChipItem(passions.get(bundle.getInt(ListSelectionFragment.RETURN_ID))));
            }
        });

        getParentFragmentManager().setFragmentResultListener(ListSelectionFragment.TAG, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Integer id = bundle.getInt(ListSelectionFragment.RETURN_ID);
                String caller = bundle.getString(ListSelectionFragment.CALLER_ID);
                Task<DataSnapshot> loadTask = load();
                switch (caller){
                    case GENDER:
                        genderID = bundle.getInt(ListSelectionFragment.RETURN_ID);
                        loadTask.continueWithTask(task -> {
                            if(task.isSuccessful()){
                                if(genderID<genders.size() && genderID>=0){
                                    binding.genderedit.setText(genders.get(genderID));
                                    return dataTransfer.setGenderId(dataTransfer.getUUID(),genderID);
                                }
                                return Tasks.forCanceled();
                            }else{
                                return Tasks.forCanceled();
                            }
                        });
                        break;
                    case PASSION:
                        loadTask.continueWithTask(task -> {
                            if(task.isSuccessful()){
                                if(id<passions.size() && id>=0){
                                    passionChips.add(new ChipItem(passions.get(id), R.drawable.ic_baseline_cancel_24,id));
                                    adapter.UpdateList(passionChips);
                                    return dataTransfer.setPassions(
                                            dataTransfer.getUUID(),
                                            ChipItemMapper.mapListChipItemToListInteger(passionChips, passions)
                                            );
                                }
                                return Tasks.forCanceled();
                            }else{
                                return Tasks.forCanceled();
                            }
                        });
                        break;
                    case ORIENTATION:
                        orientationID = bundle.getInt(ListSelectionFragment.RETURN_ID);
                        loadTask.continueWithTask(task -> {
                            if(task.isSuccessful()){
                                if(orientationID<orientations.size() && orientationID>=0){
                                    binding.orientationedit.setText(orientations.get(orientationID));
                                    return dataTransfer.setOrientationId(dataTransfer.getUUID(),orientationID);
                                }
                                return Tasks.forCanceled();
                            }else{
                                return Tasks.forCanceled();
                            }
                        });
                        break;
                }

            }
        });


        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);

        adapter = new RecyclerChipsViewAdapter(passionChips, getContext(), (value, position) -> {
            if(position==passionChips.size()){
                ArrayList<String> withoutSelected= new ArrayList<>(passions);
                Bundle args = new Bundle();

                args.putStringArrayList(ListSelectionFragment.FULL_LIST, (ArrayList<String>) passions);
                passionChips.forEach(item->{
                    withoutSelected.remove(item.getText());
                });

                args.putStringArrayList(ListSelectionFragment.LIST_WITHOUT_SELECTED,withoutSelected);
                args.putString(ListSelectionFragment.CALLER_ID,PASSION);
                navController.navigate(R.id.listSelectionFragment,args);
            }else{
                passionChips.remove(position.intValue());
                adapter.UpdateList(passionChips);
                dataTransfer.setPassions(dataTransfer.getUUID(),ChipItemMapper.mapListChipItemToListInteger(passionChips, passions));
            }
        });

        binding.passionsedit.setLayoutManager(layoutManager);
        binding.passionsedit.setAdapter(adapter);

        genders=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.genders)));

        this.binding.genderedit.setOnClickListener(v -> {
            ArrayList<String> withoutSelected= new ArrayList<>(genders);
            Bundle args = new Bundle();

            args.putStringArrayList(ListSelectionFragment.FULL_LIST, (ArrayList<String>) genders);
            if(genderID!=null && genderID<genders.size() && genderID>=0)
                withoutSelected.remove(withoutSelected.get(genderID));

            args.putStringArrayList(ListSelectionFragment.LIST_WITHOUT_SELECTED,withoutSelected);
            args.putString(ListSelectionFragment.CALLER_ID,GENDER);
            navController.navigate(R.id.listSelectionFragment,args);
        });

        orientations=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sex_orientations)));

        this.binding.orientationedit.setOnClickListener(v -> {
            ArrayList<String> withoutSelected= new ArrayList<>(orientations);
            Bundle args = new Bundle();

            args.putStringArrayList(ListSelectionFragment.FULL_LIST, (ArrayList<String>) orientations);
            if(orientationID!=null && orientationID<genders.size() && orientationID>=0)
                withoutSelected.remove(withoutSelected.get(orientationID));

            args.putStringArrayList(ListSelectionFragment.LIST_WITHOUT_SELECTED,withoutSelected);
            args.putString(ListSelectionFragment.CALLER_ID,ORIENTATION);

            navController.navigate(R.id.listSelectionFragment,args);
        });

        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController= Navigation.findNavController(view);

        photoItems = List.of(
                binding.photoedit.elementOne,
                binding.photoedit.elementTwo,
                binding.photoedit.elementThree,
                binding.photoedit.elementFour,
                binding.photoedit.elementFive,
                binding.photoedit.elementSix,
                binding.photoedit.elementSeven,
                binding.photoedit.elementEight,
                binding.photoedit.elementNine
        );

        for(var photoItem : photoItems){
            photoItem.removePhotoButton.setOnClickListener(v -> {
                photoTransfer.deletePhoto((String) photoItem.imageView.getTag())
                        .continueWithTask(deleteTask -> {
                            uris.remove(photoItem.imageView.getTag());
                            return dataTransfer.setUserPhotos(dataTransfer.getUUID(),uris).addOnCompleteListener(setPhotosTask->{
                                load();
                            });
                        });
            });
            photoItem.imageView.setOnClickListener(v->{
                if(v.getTag()==null){
                    mGetContent.launch("image/*");
                }
            });
        }

        this.mainActivityViewModel.setBottomBarVisible(false);
        this.mainActivityViewModel.setActionBarVisible(false);

        dataTransfer = new DataTransfer();

        datePicker =MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.enter_age_hint))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
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

        binding.nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dataTransfer.setName(dataTransfer.getUUID(),binding.nameEdit.getText().toString());
            }
        });
        binding.descriptionTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dataTransfer.setDescription(dataTransfer.getUUID(),binding.descriptionTextInputEditText.getText().toString());
            }
        });

        load();
    }

    private Task<DataSnapshot> load() {
        photoItems.forEach(item -> {
            item.removePhotoButton.setVisibility(View.GONE);
            item.imageView.setImageResource(R.drawable.ic_baseline_add_24);
            item.imageView.setTag(null);
        });
        return dataTransfer.getUserDetails(dataTransfer.getUUID()).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               if(binding!=null){
                   UserDetails details = Optional.ofNullable(task.getResult().getValue(UserDetails.class)).orElse(new UserDetails());
                   binding.nameEdit.setText(details.getName());
                   binding.descriptionTextInputEditText.setText(details.getDescription());
                   uris = details.getUserPhotos().getPhotos();
                   for (int i = 0; i < uris.size(); i++) {
                       Glide.with(this)
                               .load(Uri.parse(uris.get(i)))
                               .signature(new ObjectKey(uris.get(i)))
                               .into(photoItems.get(i).imageView);
                       photoItems.get(i).imageView.setTag(uris.get(i));
                       photoItems.get(i).removePhotoButton.setVisibility(View.VISIBLE);
                   }
                   passionChips=ChipItemMapper
                           .mapListIntegerToListChipItem(details.getPassions(), passions);
                   adapter.UpdateList(passionChips);
                   binding.ageTextInputEditText.setText(details.getBirthdayDate());
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
                   binding.genderedit.setText(genders.get(details.getGenderId()));
                   binding.orientationedit.setText(orientations.get(details.getOrientationId()));
               }
           }else{
               load();
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
        dataTransfer.setBirthdayDate(
                dataTransfer.getUUID(),
                binding.ageTextInputEditText.getText().toString()
        );
    }

}
