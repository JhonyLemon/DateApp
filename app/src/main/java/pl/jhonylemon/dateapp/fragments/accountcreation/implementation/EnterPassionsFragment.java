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
import pl.jhonylemon.dateapp.adapters.RecyclerChipsViewAdapter;

import pl.jhonylemon.dateapp.databinding.FragmentEnterPassionsBinding;
import pl.jhonylemon.dateapp.fragments.accountcreation.AccountCreationFragment;
import pl.jhonylemon.dateapp.mappers.ChipItemMapper;
import pl.jhonylemon.dateapp.models.ChipItem;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EnterPassionsFragment extends AccountCreationFragment {

    public static final String TAG = "EnterPassionsFragment";
    private static final Integer ProgressBarProgress = 6;
    private FragmentEnterPassionsBinding binding;

    private List<String> passions;
    private List<ChipItem> passionChips;
    private RecyclerChipsViewAdapter adapter;

    public EnterPassionsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterPassionsBinding.inflate(inflater, container, false);

        passions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.passions)));

        passionChips = new ArrayList<>();

        binding.next.setOnClickListener(this::moveNext);
        binding.previous.setOnClickListener(this::movePrevious);

        getParentFragmentManager().setFragmentResultListener(ListSelectionFragment.TAG, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                load().continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        passionChips.add(new ChipItem(passions.get(bundle.getInt(ListSelectionFragment.RETURN_ID))));
                        return save();
                    } else {
                        return Tasks.forCanceled();
                    }
                }).addOnCompleteListener(task -> {
                    validateAndEnableNext();
                });
            }
        });

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);

        adapter = new RecyclerChipsViewAdapter(passionChips, getContext(), (value, position) -> {
            if (position == passionChips.size()) {
                moveToListSelection();
            } else {
                passionChips.remove(position.intValue());
                validate();
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
        binding = null;
    }

    @Override
    protected void moveToListSelection() {
        ArrayList<String> withoutSelected = new ArrayList<>(passions);
        Bundle args = new Bundle();

        args.putStringArrayList(ListSelectionFragment.FULL_LIST, (ArrayList<String>) passions);
        if (passionChips != null && passionChips.size() > 0)
            passionChips.forEach(item -> {
                withoutSelected.remove(item.getText());
            });

        args.putStringArrayList(ListSelectionFragment.LIST_WITHOUT_SELECTED, withoutSelected);
        this.getNavController().navigate(R.id.listSelectionFragment, args);
    }

    @Override
    protected Task<DataSnapshot> load() {
        return dataTransfer.getPassions(dataTransfer.getUUID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(binding!=null) {
                    passionChips = ChipItemMapper.mapListIntegerToListChipItem(
                            Optional.ofNullable(
                                    (ArrayList<Long>) task.getResult().getValue()
                            ).orElse(new ArrayList<Long>()),
                            passions
                    );
                    adapter.UpdateList(passionChips);
                    validateAndEnableNext();
                }
            } else {
                load();
            }
        });
    }

    @Override
    protected Task<Void> save() {
        return dataTransfer.setPassions(dataTransfer.getUUID(), ChipItemMapper.mapListChipItemToListInteger(passionChips, passions)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(binding!=null) {
                    binding.next.setEnabled(getValid());
                }
            } else {
                save();
            }
        });
    }

    @Override
    protected void moveNext(View view) {
        this.getNavController().navigate(R.id.enterDescriptionFragment);
    }


    @Override
    protected Boolean validate() {
        if (passionChips != null) {
            if (passionChips.size() >= 5) {
                setValid(true);
            } else {
                setValid(false);
                if(binding!=null)
                    binding.next.setEnabled(getValid());
            }
        }
        return this.getValid();
    }

    @Override
    protected Boolean validateAndEnableNext() {
        boolean valid = validate();
        if (valid) {
            binding.next.setEnabled(getValid());
        }
        return valid;
    }
}
