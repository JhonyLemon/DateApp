package com.example.dateapp.fragments.accountcreation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dateapp.adapters.RecyclerSearchableDefaultViewAdapter;
import com.example.dateapp.databinding.FragmentEnterSelectionListBinding;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class ListSelectionFragment extends Fragment implements SearchView.OnQueryTextListener {

    public static final String TAG="ListSelectionFragment";
    public static final String FULL_LIST="FULL_LIST";
    public static final String LIST_WITHOUT_SELECTED="LIST_WITHOUT_SELECTED";
    public static final String CALLER_ID="CALLER_ID";
    public static final String RETURN_ID="RETURN_ID";

    private List<String> fullList;
    private List<String> withoutSelectedList;
    private String CALLER;

    private FragmentEnterSelectionListBinding binding;

    private RecyclerSearchableDefaultViewAdapter recyclerViewAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEnterSelectionListBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if(bundle!=null){
           fullList=bundle.getStringArrayList(FULL_LIST);
           withoutSelectedList=bundle.getStringArrayList(LIST_WITHOUT_SELECTED);
           CALLER=bundle.getString(CALLER_ID);
        }

        recyclerViewAdapter = new RecyclerSearchableDefaultViewAdapter(
                (ArrayList<String>) withoutSelectedList,
                getContext(),
                (value, position) -> {
                    Bundle result = new Bundle();
                    result.putInt(RETURN_ID, fullList.indexOf(value));
                    result.putString(CALLER_ID, CALLER);
                    getParentFragmentManager().setFragmentResult(TAG, result);
                    getActivity().onBackPressed();
                });

        binding.recyclerView.setAdapter(recyclerViewAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.addItemDecoration(new MaterialDividerItemDecoration(getContext(),MaterialDividerItemDecoration.VERTICAL));

        binding.searchView.setOnQueryTextListener(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        recyclerViewAdapter.getFilter().filter(newText);
        return true;
    }
}