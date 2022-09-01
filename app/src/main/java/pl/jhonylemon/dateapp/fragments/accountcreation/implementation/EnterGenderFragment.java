package pl.jhonylemon.dateapp.fragments.accountcreation.implementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.databinding.FragmentEnterGenderBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreation;
import pl.jhonylemon.dateapp.fragments.accountcreation.ListSelectionFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnterGenderFragment extends AccountCreation {

    public static final String TAG="EnterGenderFragment";
    private static final Integer ProgressBarProgress = 4;
    private FragmentEnterGenderBinding binding;

    private List<String> genders;
    private Integer genderID=null;

    public EnterGenderFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterGenderBinding.inflate(inflater, container, false);

        genders=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.genders)));

        binding.next.setOnClickListener(this::moveNext);
        binding.previous.setOnClickListener(this::movePrevious);
        binding.genderOther.setOnClickListener(this::selectGender);
        binding.genderFemale.setOnClickListener(this::selectGender);
        binding.genderMale.setOnClickListener(this::selectGender);
        binding.genderDontWantToTell.setOnClickListener(this::selectGender);

        getParentFragmentManager().setFragmentResultListener(ListSelectionFragment.TAG, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                genderID = bundle.getInt(ListSelectionFragment.RETURN_ID);
                if(validate())
                    save();
                load();
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

    private void uncheckAll(){
        binding.genderDontWantToTell.setChecked(false);
        binding.genderOther.setChecked(false);
        binding.genderMale.setChecked(false);
        binding.genderFemale.setChecked(false);
        binding.genderOther.setText(R.string.enter_other);
    }

    private void selectGender(View view)
    {
        uncheckAll();
        switch (view.getId()){
            case R.id.genderOther:
                moveToListSelection();
                break;
            case R.id.genderMale:
                binding.genderMale.setChecked(true);
                genderID=0;
                break;
            case R.id.genderFemale:
                binding.genderFemale.setChecked(true);
                genderID=1;
                break;
            case R.id.genderDontWantToTell:
                binding.genderDontWantToTell.setChecked(true);
                genderID=2;
                break;
            default:

                break;
        }
        if (validate())
            save();
    }

    @Override
    protected void moveToListSelection(){
        ArrayList<String> withoutSelected= new ArrayList<>(genders);
        Bundle args = new Bundle();

        args.putStringArrayList(ListSelectionFragment.FULL_LIST, (ArrayList<String>) genders);

        withoutSelected.remove(0);
        withoutSelected.remove(1);
        withoutSelected.remove(2);
        if (genderID!=null && genderID>2)
            withoutSelected.remove(genderID);

        args.putStringArrayList(ListSelectionFragment.LIST_WITHOUT_SELECTED,withoutSelected);
        this.getNavController().navigate(R.id.listSelectionFragment,args);
    }



    @Override
    protected void load() {
       uncheckAll();
       genderID=this.getAuthenticationViewModel().getGender();
       if(genderID!=null){
           if(genderID.equals(0)){
               binding.genderMale.setChecked(true);
           }else if (genderID.equals(1)){
               binding.genderFemale.setChecked(true);
           }else if (genderID.equals(2)){
               binding.genderDontWantToTell.setChecked(true);
           } else{
               binding.genderOther.setChecked(true);
               binding.genderOther.setText(genders.get(genderID));
           }
       }
    }

    @Override
    protected void save() {
        this.getAuthenticationViewModel().setGender(genderID);
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterOrientationFragment);
    }


    @Override
    protected Boolean validate() {
        if(genderID!=null) {
            this.setValid(true);
            binding.next.setEnabled(this.getValid());
            return this.getValid();
        }
        this.setValid(false);
        binding.next.setEnabled(this.getValid());
        return this.getValid();
    }
}
