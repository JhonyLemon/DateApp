package pl.jhonylemon.dateapp.fragments.main.implementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.adapters.RecyclerChipViewAdapter;
import pl.jhonylemon.dateapp.databinding.FragmentProfileInfoBinding;
import pl.jhonylemon.dateapp.databinding.GridviewPhotoItemBinding;
import pl.jhonylemon.dateapp.entity.UserDetails;
import pl.jhonylemon.dateapp.fragments.main.MainFragment;
import pl.jhonylemon.dateapp.mappers.ChipItemMapper;
import pl.jhonylemon.dateapp.models.ChipItem;
import pl.jhonylemon.dateapp.entity.UserData;
import pl.jhonylemon.dateapp.utils.DataTransfer;

public class ProfileInfoFragment extends MainFragment {

    public static final String TAG="ProfileInfoFragment";
    public static final String KEY = "UUID";
    private FragmentProfileInfoBinding binding;
    private final NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.profileEditFragment,true).build();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private String uuid;

    DataTransfer dataTransfer = new DataTransfer();

    private List<String> genders;
    private List<String> orientations;
    private List<String> passions;
    private List<ChipItem> passionChips;
    private RecyclerChipViewAdapter adapter;
    private List<GridviewPhotoItemBinding> photoItems;

    public ProfileInfoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileInfoBinding.inflate(inflater, container, false);

        binding.nameEdit.setEnabled(false);

        Bundle bundle = getArguments();
        if(bundle!=null){
            uuid=bundle.getString(KEY,"");
        }
        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController= Navigation.findNavController(view);
        this.mainActivityViewModel.setBottomBarVisible(false);
        this.mainActivityViewModel.setActionBarVisible(false);

        passions=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.passions)));
        passionChips = new ArrayList<>();
        genders=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.genders)));
        orientations=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sex_orientations)));

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);

        adapter = new RecyclerChipViewAdapter(passionChips,getContext());

        binding.passionsedit.setLayoutManager(layoutManager);
        binding.passionsedit.setAdapter(adapter);

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

        if(!uuid.isEmpty()){
            load();
        }else{
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    public void load(){
        photoItems.forEach(view->{
            view.imageView.setImageDrawable(null);
            view.imageView.setVisibility(View.GONE);
        });
        dataTransfer.getUserDetails(uuid).get().addOnSuccessListener(dataSnapshot -> {
            UserDetails data = dataSnapshot.getValue(UserDetails.class);
            this.binding.descriptionTextInputEditText.setText(data.getDescription());
            this.binding.nameEdit.setText(data.getName());
            this.binding.ageTextInputEditText.setText(data.getBirthdayDate());
            passionChips=ChipItemMapper
                    .mapListIntegerToListChipItem(data.getPassions(),passions);
            adapter.UpdateList(passionChips);

            this.binding.genderedit.setText(genders.get(data.getGenderId()));

            this.binding.orientationedit.setText(orientations.get(data.getOrientationId()));

            List<String> urls = data.getUserPhotos().getPhotos();
            for(int i=0; i<urls.size(); i++){
                Glide.with(getContext())
                                .load(urls.get(i))
                                        .signature(new ObjectKey(urls.get(i)))
                                                .into(photoItems.get(i).imageView);
                photoItems.get(i).imageView.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void move(int id) {
        navController.navigate(id,null,navOptions);
    }
}