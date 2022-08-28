package com.example.dateapp.fragments.accountcreation.implementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import com.example.dateapp.R;
import com.example.dateapp.adapters.RecyclerChipsViewAdapter;
import com.example.dateapp.databinding.FragmentEnterPassionsBinding;

import com.example.dateapp.fragments.accountcreation.AccountCreation;
import com.example.dateapp.fragments.accountcreation.ListSelectionFragment;
import com.example.dateapp.mappers.ChipItemMapper;
import com.example.dateapp.mappers.ChipItemMapperImpl;
import com.example.dateapp.models.ChipItem;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnterPassionsFragment extends AccountCreation {

    public static final String TAG="EnterPassionsFragment";
    private static final Integer ProgressBarProgress = 6;
    private FragmentEnterPassionsBinding binding;

    private List<String> passions;

    private List<ChipItem> passionChips;

    private ChipItemMapper mapper = new ChipItemMapperImpl();

    private RecyclerChipsViewAdapter adapter;


    public EnterPassionsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterPassionsBinding.inflate(inflater, container, false);

        passions=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.passions)));

        passionChips = new ArrayList<>();

        binding.next.setOnClickListener(this::moveNext);
        binding.previous.setOnClickListener(this::movePrevious);

        getParentFragmentManager().setFragmentResultListener(ListSelectionFragment.TAG, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                passionChips.add(new ChipItem(passions.get(bundle.getInt(ListSelectionFragment.RETURN_ID))));
                if(validate())
                    save();
                load();
            }
        });


        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);

        adapter = new RecyclerChipsViewAdapter(passionChips, getContext(), (value, position) -> {
            if(position== passionChips.size()){
                moveToListSelection();
            }else{
                passionChips.remove(position.intValue());
                if(validate())
                    save();
                adapter.UpdateList(passionChips);
            }
        });

        binding.recyclerViewPassions.setLayoutManager(layoutManager);
        binding.recyclerViewPassions.setAdapter(adapter);

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
    protected void moveToListSelection() {
        ArrayList<String> withoutSelected= new ArrayList<>(passions);
        Bundle args = new Bundle();

        args.putStringArrayList(ListSelectionFragment.FULL_LIST, (ArrayList<String>) passions);

        if (passionChips!=null && passionChips.size()>0)
            passionChips.forEach(item->{
                withoutSelected.remove(item.getText());
            });

        args.putStringArrayList(ListSelectionFragment.LIST_WITHOUT_SELECTED,withoutSelected);
        this.getNavController().navigate(R.id.listSelectionFragment,args);
    }

    @Override
    protected void load() {
      passionChips=mapper
              .mapListIntegerToListChipItem(this.getAuthenticationViewModel().getPassions(),passions);
      adapter.UpdateList(passionChips);
    }

    @Override
    protected void save() {
        this.getAuthenticationViewModel().setPassions(mapper.mapListChipItemToListInteger(passionChips,passions));
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterDescriptionFragment);
    }


    @Override
    protected Boolean validate() {
        if(passionChips!=null ){
            if(passionChips.size()>=5)
                binding.next.setEnabled(true);
            else
                binding.next.setEnabled(false);
        }
        this.setValid(true);
        return this.getValid();
    }
}
